package info.solidsoft.gradle.cdeliveryboy.infra.task

import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.CDeliveryBoyCiPrepareTask
import info.solidsoft.gradle.cdeliveryboy.infra.ReleaseVersionDeterminer
import info.solidsoft.gradle.cdeliveryboy.logic.BuildConditionEvaluator
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesValidator
import info.solidsoft.gradle.cdeliveryboy.logic.config.TaskConfig
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

import java.lang.invoke.MethodHandles

@CompileStatic
class PrepareForCiBuildTaskOrchestrator extends AbstractCiTaskOrchestrator {

    private static Logger log = Logging.getLogger(MethodHandles.lookup().lookupClass())

    private final TaskConfig taskConfig
    private final BuildConditionEvaluator buildConditionEvaluator
    private final CiVariablesValidator ciVariablesValidator
    private final ReleaseVersionDeterminer releaseVersionDeterminer

    PrepareForCiBuildTaskOrchestrator(Project project, TaskConfig taskConfig,
                                      BuildConditionEvaluator buildConditionEvaluator, CiVariablesValidator ciVariablesValidator,
                                      ReleaseVersionDeterminer releaseVersionDeterminer) {
        super(project)
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
            log.info("'${prepareTask.name}' task will not be executed")
        }
    }
}
