package info.solidsoft.gradle.cdeliveryboy.infra.ioc

import info.solidsoft.gradle.cdeliveryboy.infra.DependantPluginsConfigurer
import info.solidsoft.gradle.cdeliveryboy.infra.task.TaskCreator

interface EarlyIocContext {

    TaskCreator getTaskCreator()

    DependantPluginsConfigurer getDependantPluginsConfigurer()
}
