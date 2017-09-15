package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import org.gradle.api.Project

@CompileStatic
@PackageScope
class CiBuildTaskInstantiator implements CDeliveryBoyPluginConstants {

    private final Project project
    private final CDeliveryBoyPluginConfig pluginConfig

    CiBuildTaskInstantiator(Project project, CDeliveryBoyPluginConfig pluginConfig) {
        this.project = project
        this.pluginConfig = pluginConfig
    }

    CDeliveryBoyCiBuildTask createAndConfigureAndReturn() {
        CDeliveryBoyCiBuildTask buildTask = createCiBuildTasks(project)
        configureBuildTask(buildTask, pluginConfig)
        return buildTask
    }

    private CDeliveryBoyCiBuildTask createCiBuildTasks(Project project) {
        return project.tasks.create(CI_BUILD_TASK_NAME, CDeliveryBoyCiBuildTask).with {
            description = "Performs CI build with optional release"
            group = RELEASE_TASKS_GROUP_NAME
            return it
        }
    }

    @CompileDynamic
    private Closure<String> configureBuildTask(CDeliveryBoyCiBuildTask buildTask, CDeliveryBoyPluginConfig pluginConfig) {
        buildTask.conventionMapping.with {
            //TODO
        }
    }
}
