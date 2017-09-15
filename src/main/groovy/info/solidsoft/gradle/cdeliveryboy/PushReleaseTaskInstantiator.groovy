package info.solidsoft.gradle.cdeliveryboy

import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import org.gradle.api.Project

class PushReleaseTaskInstantiator implements CDeliveryBoyPluginConstants {

    private final Project project
    private final CDeliveryBoyPluginConfig pluginConfig

    PushReleaseTaskInstantiator(Project project, CDeliveryBoyPluginConfig pluginConfig) {
        this.project = project
        this.pluginConfig = pluginConfig
    }

    PushRelease2Task createPushRelease2Task() {
        return project.tasks.create(PUSH_RELEASE2_TASK_NAME, PushRelease2Task).with {
            description = "Performs second stage of release - pushes tag to remote (workaround on Axion limitation with refspec)"
            group = RELEASE_TASKS_GROUP_NAME
            return it
        }
    }
}
