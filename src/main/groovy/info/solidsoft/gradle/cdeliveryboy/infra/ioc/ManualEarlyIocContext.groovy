package info.solidsoft.gradle.cdeliveryboy.infra.ioc

import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.infra.DependantPluginsConfigurer
import info.solidsoft.gradle.cdeliveryboy.infra.task.TaskCreator
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import org.gradle.api.Project

@CompileStatic
class ManualEarlyIocContext implements EarlyIocContext {

    private final Project project
    private final CDeliveryBoyPluginConfig pluginConfig

    private TaskCreator taskCreator
    private DependantPluginsConfigurer dependantPluginsConfigurer

    ManualEarlyIocContext(Project project, CDeliveryBoyPluginConfig pluginConfig) {
        this.project = project
        this.pluginConfig = pluginConfig
    }

    @Override
    TaskCreator getTaskCreator() {
        return taskCreator
    }

    @Override
    DependantPluginsConfigurer getDependantPluginsConfigurer() {
        return dependantPluginsConfigurer
    }

    void initialize() {
        taskCreator = new TaskCreator(project)
        dependantPluginsConfigurer = new DependantPluginsConfigurer(project)
    }
}
