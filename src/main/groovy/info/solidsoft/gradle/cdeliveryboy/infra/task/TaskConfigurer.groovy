package info.solidsoft.gradle.cdeliveryboy.infra.task

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.CDeliveryBoyCiBuildTask
import info.solidsoft.gradle.cdeliveryboy.CDeliveryBoyCiPrepareTask
import info.solidsoft.gradle.cdeliveryboy.PushRelease2Task
import info.solidsoft.gradle.cdeliveryboy.infra.PropertyReader
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesConfig
import org.gradle.api.Project

//TODO: Consider merging it with *Orchestrators
@CompileStatic
class TaskConfigurer {

    private final Project project
    private final CDeliveryBoyPluginConfig pluginConfig
    private final CiVariablesConfig ciVariablesConfig
    private final PropertyReader propertyReader

    TaskConfigurer(Project project, CDeliveryBoyPluginConfig pluginConfig, CiVariablesConfig ciVariablesConfig, PropertyReader propertyReader) {
        this.project = project
        this.pluginConfig = pluginConfig
        this.ciVariablesConfig = ciVariablesConfig
        this.propertyReader = propertyReader
    }

    @CompileDynamic
    void configurePrepareCiBuildTask(CDeliveryBoyCiPrepareTask prepareTask) {
        prepareTask.conventionMapping.with {
            ciType = { pluginConfig.ciType }
            releaseBranch = { pluginConfig.git.releaseBranch }
        }
    }

    @CompileDynamic
    void configureCiBuildTask(CDeliveryBoyCiBuildTask buildTask) {
        //None at the moment
    }

    @CompileDynamic
    void configurePushRelease2Task(PushRelease2Task pushRelease2Task) {
        pushRelease2Task.conventionMapping.with {
            repoAsSlug = { propertyReader.findByName(ciVariablesConfig.repoSlugName) }
            releaseBranch = { pluginConfig.git.releaseBranch }
        }
    }
}
