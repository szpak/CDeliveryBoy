package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.CompileStatic
import org.gradle.api.internal.ConventionTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

import java.lang.invoke.MethodHandles

@CompileStatic
class CDeliveryBoyCiBuildTask extends ConventionTask {

    private static Logger log = Logging.getLogger(MethodHandles.lookup().lookupClass())

    //TODO: Manage inputs/outputs and up-to-date state
    @Input
    boolean inReleaseMode = false
    @Input
    String modeConditions

    CDeliveryBoyCiBuildTask() {
        //For time being should be re-executed every time - however, all dependent tasks (potentially) could be up-to-date
        this.outputs.upToDateWhen { false }
    }

    @TaskAction
    void displayReleaseModeConditionsAsEverythingElseShouldBeAlreadyDoneInDependantTasks() {
        log.lifecycle(modeConditions)
    }

    @Deprecated
    @Input  //to do not generate warning
    boolean getIsInReleaseMode() {
        log.warn("DEPRECATION WARNING. 'isInReleaseMode' property is deprecated. Use 'inReleaseMode'")
        return inReleaseMode
    }
}
