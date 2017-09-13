package info.solidsoft.gradle.cdeliveryboy.infra

import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.infra.config.DefaultProjectConfig
import info.solidsoft.gradle.cdeliveryboy.logic.BuildConditionEvaluator
import info.solidsoft.gradle.cdeliveryboy.logic.PropertyOverrider
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesValidator
import info.solidsoft.gradle.cdeliveryboy.logic.config.DryRunTaskConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.ProjectConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.TaskConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.TravisVariablesConfig
import org.gradle.api.Project

@CompileStatic
class ManualIocContext implements IocContext {

    private final Project project
    private final CDeliveryBoyPluginConfig pluginConfig

    private PropertyOverrider propertyOverrider
    private TaskConfig taskConfig
    private CiVariablesConfig ciVariablesConfig
    private PropertyReader envVariableReader

    private BuildConditionEvaluator buildConditionEvaluator
    private CiVariablesValidator ciVariablesValidator
    private ReleaseVersionDeterminer releaseVersionDeterminer

    ManualIocContext() {    //TODO: Remove it when https://github.com/spockframework/spock/issues/769 is fixed
        this(null, null)
    }

    ManualIocContext(Project project, CDeliveryBoyPluginConfig pluginConfig) {
        this.project = project
        this.pluginConfig = pluginConfig
    }

    void initialize() {
        propertyOverrider = new PropertyOverrider(new ProjectPropertyReader(project))
        propertyOverrider.applyCommandLineProperties(pluginConfig)

        taskConfig = createTaskConfigOrFail(pluginConfig) //TODO: Make it a part of public configuration?
        ciVariablesConfig = createCiVariablesConfigOrFail(pluginConfig)

        envVariableReader = new EnvironmentVariableReader()
        ProjectConfig projectConfig = new DefaultProjectConfig(project)
        OverriddenVersionInCommitMessageFinder overriddenVersionDeterminer = new OverriddenVersionInCommitMessageFinder()
        buildConditionEvaluator = new BuildConditionEvaluator(ciVariablesConfig, pluginConfig, envVariableReader, projectConfig,
                overriddenVersionDeterminer)
        ciVariablesValidator = new CiVariablesValidator(envVariableReader, ciVariablesConfig)
        releaseVersionDeterminer = new ReleaseVersionDeterminer(AxionReleaseVersionOverrider.forProject(project))
    }

    private TaskConfig createTaskConfigOrFail(CDeliveryBoyPluginConfig pluginConfig) {
        return pluginConfig.dryRun ? new DryRunTaskConfig() : pluginConfig.tasks
    }

    private TravisVariablesConfig createCiVariablesConfigOrFail(CDeliveryBoyPluginConfig pluginConfig) {
        if (pluginConfig.ciType != 'travis') {
            throw new UnsupportedOperationException("Unsupported CI type: ${pluginConfig.ciType}. Currently only 'travis' is supported.")
        }
        return new TravisVariablesConfig()
    }

    @Override
    PropertyOverrider getPropertyOverrider() {
        return propertyOverrider
    }

    @Override
    TaskConfig getTaskConfig() {
        return taskConfig
    }

    @Override
    CiVariablesConfig getCiVariablesConfig() {
        return ciVariablesConfig
    }

    @Override
    PropertyReader getEnvVariableReader() {
        return envVariableReader
    }

    @Override
    BuildConditionEvaluator getBuildConditionEvaluator() {
        return buildConditionEvaluator
    }

    @Override
    CiVariablesValidator getCiVariablesValidator() {
        return ciVariablesValidator
    }

    @Override
    ReleaseVersionDeterminer getReleaseVersionDeterminer() {
        return releaseVersionDeterminer
    }
}
