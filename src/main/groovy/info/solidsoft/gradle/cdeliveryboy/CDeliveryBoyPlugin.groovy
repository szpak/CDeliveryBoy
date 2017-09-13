package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.infra.DependantPluginsConfigurer
import info.solidsoft.gradle.cdeliveryboy.infra.IocContext
import info.solidsoft.gradle.cdeliveryboy.infra.ManualIocContext
import info.solidsoft.gradle.cdeliveryboy.infra.PropertyReader
import info.solidsoft.gradle.cdeliveryboy.infra.ReleaseVersionDeterminer
import info.solidsoft.gradle.cdeliveryboy.logic.BuildConditionEvaluator
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesValidator
import info.solidsoft.gradle.cdeliveryboy.logic.config.TaskConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.tooling.BuildException

@CompileStatic
@SuppressWarnings("GrMethodMayBeStatic")
class CDeliveryBoyPlugin implements Plugin<Project> {

    private static Logger log = Logging.getLogger(CDeliveryBoyPlugin)

    public static final String EXTENSION_NAME = "cDeliveryBoy"

    private static final String PREPARE_FOR_CI_BUILD_TASK_NAME = "prepareForCiBuild"
    private static final String CI_BUILD_TASK_NAME = "ciBuild"
    private static final String PUSH_RELEASE2_TASK_NAME = "pushRelease2"

    private static final String RELEASE_TASKS_GROUP_NAME = "release"

    private Project project //???

    private IocContext iocContext   //as a field to make it modifiable in integration tests through enhanceOrReplaceContextHack

    @Override
    void apply(Project project) {
        this.project = project

        //TODO: Casting is required due to: https://issues.apache.org/jira/browse/GROOVY-7907 - fixed in Groovy 2.4.8
        CDeliveryBoyPluginConfig pluginConfig = (CDeliveryBoyPluginConfig) project.extensions.create(EXTENSION_NAME, CDeliveryBoyPluginConfig) //TODO: one or more extensions?
        //TODO: It would be better to create placeholder/dummy tasks with given name on demand (e.g. for testing and reporting)
        CDeliveryBoyCiPrepareTask prepareTask = createPrepareForCiBuildTask(project) //One method to create and configure?
        configurePrepareTask(prepareTask, pluginConfig)
        CDeliveryBoyCiBuildTask buildTask = createCiBuildTasks(project)
        configureBuildTask(buildTask, pluginConfig)
        PushRelease2Task pushRelease2Task = createPushRelease2Task(project)

        new DependantPluginsConfigurer(project).applyAndPreconfigureIfNeeded()  //TODO: Not in context as it's to early

        iocContext = new ManualIocContext(project, pluginConfig)

        project.afterEvaluate {

            iocContext.initialize()
            iocContext.with {
                propertyOverrider.applyCommandLineProperties(pluginConfig)
                setDependantTasksForPrepareTask(prepareTask, taskConfig, buildConditionEvaluator, ciVariablesValidator, releaseVersionDeterminer)   //TODO: Maybe create some common object to keep plugin configuration?
                setDependantTasksForBuildTask(pluginConfig, buildTask, taskConfig, buildConditionEvaluator, ciVariablesValidator)
                configurePushRelease2Task(pushRelease2Task, pluginConfig, ciVariablesConfig, envVariableReader)
            }
        }
    }

    private CDeliveryBoyCiBuildTask createCiBuildTasks(Project project) {
        return project.tasks.create(CI_BUILD_TASK_NAME, CDeliveryBoyCiBuildTask).with {
            description = "Performs CI build with optional release"
            group = RELEASE_TASKS_GROUP_NAME
            return it
        }
    }

    private CDeliveryBoyCiPrepareTask createPrepareForCiBuildTask(Project project) {
        return project.tasks.create(PREPARE_FOR_CI_BUILD_TASK_NAME, CDeliveryBoyCiPrepareTask).with {
            description = "Prepares for CI build with optional release"
            group = RELEASE_TASKS_GROUP_NAME
            return it
        }
    }

    private PushRelease2Task createPushRelease2Task(Project project) {
        return project.tasks.create(PUSH_RELEASE2_TASK_NAME, PushRelease2Task).with {
            description = "Performs second stage of release - pushes tag to remote (workaround on Axion limitation with refspec)"
            group = RELEASE_TASKS_GROUP_NAME
            return it
        }
    }

    private void setDependantTasksForPrepareTask(CDeliveryBoyCiPrepareTask prepareTask, TaskConfig taskConfig,
                                                 BuildConditionEvaluator buildConditionEvaluator, CiVariablesValidator ciVariablesValidator,
                                                 ReleaseVersionDeterminer releaseVersionDeterminer) {

        if (isGivenTaskExpectedToBeExecuted(prepareTask)) {
            ciVariablesValidator.checkExistence()
            prepareTask.modeConditions = buildConditionEvaluator.releaseConditionsAsString
            prepareTask.inReleaseMode = buildConditionEvaluator.inReleaseMode

            if (buildConditionEvaluator.inReleaseMode) {
                releaseVersionDeterminer.determineAndOverrideReleaseVersionIfRequested(buildConditionEvaluator.overriddenVersion())
                prepareTask.dependsOn(getJustOneTaskByNameOrFail(taskConfig.createReleaseTask))
            }
        } else {
            log.lifecycle("'${prepareTask.name}' task will not be executed") //TODO: Switch to info
        }
    }

    private boolean isGivenTaskExpectedToBeExecuted(Task taskToChecked) {
        //Task graph would be more reliable, but it's available only after afterEvaluate phrase (in addition it's problematic to test with ProjectBuilder)
        //toLowerCase as Gradle permits it when tasks are called from command line
        List<String> requiredTaskNamesAsLowerCase = project.gradle.startParameter.taskNames.collect { it.toLowerCase() }
        return requiredTaskNamesAsLowerCase.contains(taskToChecked.name.toLowerCase())
    }

    private void setDependantTasksForBuildTask(CDeliveryBoyPluginConfig pluginConfig, CDeliveryBoyCiBuildTask ciBuildTask, TaskConfig taskConfig,
                                               BuildConditionEvaluator buildConditionEvaluator, CiVariablesValidator ciVariablesValidator) {

        if (isGivenTaskExpectedToBeExecuted(ciBuildTask)) {
            ciVariablesValidator.checkExistence()
            ciBuildTask.modeConditions = buildConditionEvaluator.releaseConditionsAsString
            ciBuildTask.inReleaseMode = buildConditionEvaluator.inReleaseMode

            ciBuildTask.dependsOn(taskConfig.buildProjectTask)
            if (buildConditionEvaluator.inReleaseBranch) {
                ciBuildTask.dependsOn(taskConfig.uploadArchivesTask)   //TODO: Support skipping for snapshots if set in configuration
            }
            if (buildConditionEvaluator.inReleaseMode) {
                if (!buildConditionEvaluator.isSnapshotVersion()) {
                    setDependantTasksForBuildTaskInReleaseMode(pluginConfig, taskConfig, ciBuildTask)
                } else {
                    throw new BuildException("Release triggered, but still in snapshot version. " +
                            "Has '$PREPARE_FOR_CI_BUILD_TASK_NAME' task been executed in separate Gradle call before?", null)
                }
            }
        } else {
            log.lifecycle("'${ciBuildTask.name}' task will not be executed.")  //TODO: Switch to info
        }

    }

    private void setDependantTasksForBuildTaskInReleaseMode(CDeliveryBoyPluginConfig pluginConfig, TaskConfig taskConfig, CDeliveryBoyCiBuildTask ciBuildTask) {
        Task closeRepositoryTask = getJustOneTaskByNameOrFail(taskConfig.closeRepositoryTask)
        Task pushReleaseTask = getJustOneTaskByNameOrFail(taskConfig.pushReleaseTask)

        ciBuildTask.dependsOn(closeRepositoryTask)
        ciBuildTask.dependsOn(pushReleaseTask)
        closeRepositoryTask.mustRunAfter(taskConfig.uploadArchivesTask)
        pushReleaseTask.mustRunAfter(taskConfig.closeRepositoryTask)
        //TODO: Add cleanup task after pushReleaseTask failure as finalizedBy + " onlyIf { pushReleaseTask.state.failure != null } " - to not to keep open staging repositories

        //TODO: Extract to a separate method
        Task promoteRepositoryTask = getTaskByNameOrCreateAndUsePlaceholderIfNotSetOrFailIfNotAvailable(taskConfig.promoteRepositoryTask, "promoteRepositoryTask")
        ciBuildTask.dependsOn(promoteRepositoryTask)
        promoteRepositoryTask.mustRunAfter(taskConfig.pushReleaseTask)
        if (!pluginConfig.nexus.autoPromote) {
            //TODO: Try to display it next to promoteRepository task - in ciBuild task execution?
            log.lifecycle("NOTE: Artifacts auto-promotion disabled in configuration. Execute 'promoteRepository' task manually to trigger promotion to Maven Central")
            promoteRepositoryTask.enabled = false
        }
    }

    private Task getTaskByNameOrCreateAndUsePlaceholderIfNotSetOrFailIfNotAvailable(String taskName, String descriptableTaskName) {
        if (taskName == null) {
            return project.tasks.create("${descriptableTaskName}Placeholder", CDeliveryBoyPlaceholderTask, { it.taskName = descriptableTaskName })
        } else {
            return getJustOneTaskByNameOrFail(taskName)
        }
    }

    private Task getJustOneTaskByNameOrFail(String taskName) {
        Set<Task> tasksByName = project.getTasksByName(taskName, false)
        if (tasksByName.size() != 1) {
            throw new BuildException("Expected exactly 1 task with name $taskName. Found ${tasksByName.size()}: '${tasksByName*.name}'", null)
        }
        return tasksByName.first()
    }

    @CompileDynamic
    private Closure<String> configurePrepareTask(CDeliveryBoyCiPrepareTask prepareTask, CDeliveryBoyPluginConfig pluginConfig) {
        prepareTask.conventionMapping.with {
            ciType = { pluginConfig.ciType }
            releaseBranch = { pluginConfig.git.releaseBranch }
        }
    }

    @CompileDynamic
    private Closure<String> configureBuildTask(CDeliveryBoyCiBuildTask buildTask, CDeliveryBoyPluginConfig pluginConfig) {
        buildTask.conventionMapping.with {
            //TODO
        }
    }

    @CompileDynamic
    private Closure<String> configurePushRelease2Task(PushRelease2Task pushRelease2Task, CDeliveryBoyPluginConfig pluginConfig,
                                                      CiVariablesConfig ciVariablesConfig, PropertyReader propertyReader) {
        pushRelease2Task.conventionMapping.with {
            repoAsSlug = { propertyReader.findByName(ciVariablesConfig.repoSlugName) }
            releaseBranch = { pluginConfig.git.releaseBranch }
        }
    }

    private enhanceOrReplaceContextHack(@DelegatesTo(IocContext) Closure<IocContext> f) {   //Function is not available in Java 7
        iocContext = f.call(iocContext)
    }
}
