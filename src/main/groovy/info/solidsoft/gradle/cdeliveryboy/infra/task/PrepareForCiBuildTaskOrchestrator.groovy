package info.solidsoft.gradle.cdeliveryboy.infra.task

import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.CDeliveryBoyCiPrepareTask
import info.solidsoft.gradle.cdeliveryboy.infra.ReleaseVersionDeterminer
import info.solidsoft.gradle.cdeliveryboy.logic.BuildConditionEvaluator
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesValidator
import info.solidsoft.gradle.cdeliveryboy.logic.config.TaskConfig
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.tooling.BuildException

import java.lang.invoke.MethodHandles

@CompileStatic
class PrepareForCiBuildTaskOrchestrator {

    private static Logger log = Logging.getLogger(MethodHandles.lookup().lookupClass())

    private final Project project
    private final TaskConfig taskConfig
    private final BuildConditionEvaluator buildConditionEvaluator
    private final CiVariablesValidator ciVariablesValidator
    private final ReleaseVersionDeterminer releaseVersionDeterminer

    PrepareForCiBuildTaskOrchestrator(Project project, TaskConfig taskConfig,
                                      BuildConditionEvaluator buildConditionEvaluator, CiVariablesValidator ciVariablesValidator,
                                      ReleaseVersionDeterminer releaseVersionDeterminer) {
        this.project = project
        this.taskConfig = taskConfig
        this.buildConditionEvaluator = buildConditionEvaluator
        this.ciVariablesValidator = ciVariablesValidator
        this.releaseVersionDeterminer = releaseVersionDeterminer
    }

    void orchestrateDependantTasks(CDeliveryBoyCiPrepareTask prepareTask) {

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

    private Task getJustOneTaskByNameOrFail(String taskName) {
        Set<Task> tasksByName = project.getTasksByName(taskName, false)
        if (tasksByName.size() != 1) {
            throw new BuildException("Expected exactly 1 task with name $taskName. Found ${tasksByName.size()}: '${tasksByName*.name}'", null)
        }
        return tasksByName.first()
    }
}
