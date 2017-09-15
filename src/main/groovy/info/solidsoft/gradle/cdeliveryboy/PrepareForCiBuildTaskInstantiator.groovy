package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import org.gradle.api.Project

@CompileStatic
@PackageScope
class PrepareForCiBuildTaskInstantiator implements CDeliveryBoyPluginConstants {

    private final Project project
    private final CDeliveryBoyPluginConfig pluginConfig

    PrepareForCiBuildTaskInstantiator(Project project, CDeliveryBoyPluginConfig pluginConfig) {
        this.project = project
        this.pluginConfig = pluginConfig
    }

    CDeliveryBoyCiPrepareTask createAndConfigureAndReturn() {

        //TODO: It would be better to create placeholder/dummy tasks with given name on demand (e.g. for testing and reporting)
        CDeliveryBoyCiPrepareTask prepareTask = createPrepareForCiBuildTask(project) //One method to create and configure?
        configurePrepareTask(prepareTask, pluginConfig)
        return prepareTask
    }

    private CDeliveryBoyCiPrepareTask createPrepareForCiBuildTask(Project project) {
        return project.tasks.create(PREPARE_FOR_CI_BUILD_TASK_NAME, CDeliveryBoyCiPrepareTask).with {
            description = "Prepares for CI build with optional release"
            group = RELEASE_TASKS_GROUP_NAME
            return it
        }
    }

    @CompileDynamic
    private void configurePrepareTask(CDeliveryBoyCiPrepareTask prepareTask, CDeliveryBoyPluginConfig pluginConfig) {   //TODO: Too many parameters
        prepareTask.conventionMapping.with {
            ciType = { pluginConfig.ciType }
            releaseBranch = { pluginConfig.git.releaseBranch }
        }
    }
}
