package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.CompileStatic
import org.gradle.api.internal.ConventionTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

@CompileStatic
class CDeliveryBoyCiPrepareTask extends ConventionTask {

    private static Logger log = Logging.getLogger(CDeliveryBoyCiPrepareTask)

    @Input
    String ciType   //Not used at a task level - just to let Gradle know that task is not up-to-date after its change
    @Input
    String releaseBranch

    //TODO: Manage inputs/outputs and up-to-date state
    boolean inReleaseMode = false
    String modeConditions

    @TaskAction
    void displayReleaseModeConditionsAsEverythingElseShouldBeAlreadyDoneInDependantTasks() {
        log.lifecycle(modeConditions)
    }

    @Deprecated
    boolean getIsInReleaseMode() {
        log.warn("DEPRECATION WARNING. 'isInReleaseMode' property is deprecated. Use 'inReleaseMode'")
        return inReleaseMode
    }
}
