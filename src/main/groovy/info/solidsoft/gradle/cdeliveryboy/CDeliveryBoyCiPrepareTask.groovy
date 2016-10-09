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

    boolean isInReleaseMode //TODO: What annotation as this are just an internal fields?
    String modeConditions

    @TaskAction
    void prepare() {
        if (!isInReleaseMode) {
            log.lifecycle("Not in release mode. Conditions: $modeConditions")
            //TODO: Can be set as up-to-date during execution?
            //      'this.outputs.upToDateWhen { ??? }' - when it is called?
        }
    }
}
