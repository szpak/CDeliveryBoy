package info.solidsoft.gradle.cdeliveryboy

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

@Deprecated
class CDeliveryBoyPlaceholderTask extends DefaultTask {

    private static Logger log = Logging.getLogger(CDeliveryBoyPlaceholderTask)

    @Input
    String taskName = "defaultPlaceholder"

    @TaskAction
    void doNothing() {
        log.lifecycle("Doing nothing: $taskName")
    }
}
