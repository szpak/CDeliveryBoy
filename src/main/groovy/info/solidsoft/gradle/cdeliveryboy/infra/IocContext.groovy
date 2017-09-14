package info.solidsoft.gradle.cdeliveryboy.infra

import info.solidsoft.gradle.cdeliveryboy.infra.task.PrepareForCiBuildTaskDependencer
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

    PrepareForCiBuildTaskDependencer getPrepareForCiBuildTaskDependencer()
}
