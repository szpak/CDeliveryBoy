package info.solidsoft.gradle.cdeliveryboy.infra.ioc

import info.solidsoft.gradle.cdeliveryboy.infra.task.TaskConfigurer
import info.solidsoft.gradle.cdeliveryboy.infra.PropertyReader
import info.solidsoft.gradle.cdeliveryboy.infra.ReleaseVersionDeterminer
import info.solidsoft.gradle.cdeliveryboy.infra.task.CiBuildTaskOrchestrator
import info.solidsoft.gradle.cdeliveryboy.infra.task.PrepareForCiBuildTaskOrchestrator
import info.solidsoft.gradle.cdeliveryboy.logic.BuildConditionEvaluator
import info.solidsoft.gradle.cdeliveryboy.logic.PropertyOverrider
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesValidator
import info.solidsoft.gradle.cdeliveryboy.logic.config.TaskConfig

interface IocContext {

    void initialize()

    PropertyOverrider getPropertyOverrider()

    TaskConfig getTaskConfig()

    CiVariablesConfig getCiVariablesConfig()

    PropertyReader getEnvVariableReader()

    BuildConditionEvaluator getBuildConditionEvaluator()

    CiVariablesValidator getCiVariablesValidator()

    ReleaseVersionDeterminer getReleaseVersionDeterminer()

    PrepareForCiBuildTaskOrchestrator getPrepareForCiBuildTaskOrchestrator()

    CiBuildTaskOrchestrator getCiBuildTaskOrchestrator()

    TaskConfigurer getTaskConfigurer()
}
