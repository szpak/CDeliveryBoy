package info.solidsoft.gradle.cdeliveryboy.infra.task

import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.CDeliveryBoyCiBuildTask
import info.solidsoft.gradle.cdeliveryboy.CDeliveryBoyPlaceholderTask
import info.solidsoft.gradle.cdeliveryboy.logic.BuildConditionEvaluator
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesValidator
import info.solidsoft.gradle.cdeliveryboy.logic.config.TaskConfig
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.tooling.BuildException

import java.lang.invoke.MethodHandles

import static info.solidsoft.gradle.cdeliveryboy.CDeliveryBoyPluginConstants.PREPARE_FOR_CI_BUILD_TASK_NAME

@CompileStatic
class CiBuildTaskOrchestrator extends AbstractCiTaskOrchestrator {

    private static Logger log = Logging.getLogger(MethodHandles.lookup().lookupClass())

    private final TaskConfig taskConfig
    private final BuildConditionEvaluator buildConditionEvaluator
    private final CiVariablesValidator ciVariablesValidator
    private final CDeliveryBoyPluginConfig pluginConfig

    CiBuildTaskOrchestrator(Project project, CDeliveryBoyPluginConfig pluginConfig, TaskConfig taskConfig,
                                       BuildConditionEvaluator buildConditionEvaluator, CiVariablesValidator ciVariablesValidator) {
        super(project)
        this.pluginConfig = pluginConfig
        this.taskConfig = taskConfig
        this.buildConditionEvaluator = buildConditionEvaluator
        this.ciVariablesValidator = ciVariablesValidator
    }

    void orchestrateDependantTasks(CDeliveryBoyCiBuildTask ciBuildTask) {

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
}
