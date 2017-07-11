package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.CompileStatic
import org.gradle.api.internal.ConventionTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.TaskAction

import java.lang.invoke.MethodHandles

@CompileStatic
class CDeliveryBoyCiBuildTask extends ConventionTask {

    private static Logger log = Logging.getLogger(MethodHandles.lookup().lookupClass())

    String modeConditions

    @TaskAction
    void doSomethingBetterNameNeeded() {
        log.lifecycle(modeConditions)
    }
}
