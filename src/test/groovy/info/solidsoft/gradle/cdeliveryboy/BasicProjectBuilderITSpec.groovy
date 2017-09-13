package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.PackageScope
import info.solidsoft.gradle.cdeliveryboy.infra.IocContext
import info.solidsoft.gradle.cdeliveryboy.infra.ManualIocContext
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesValidator
import info.solidsoft.gradle.cdeliveryboy.logic.config.DryRunTaskConfig
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

@PackageScope
abstract class BasicProjectBuilderITSpec extends Specification implements TaskFixtureTrait, ProjectAware, TaskTestTrait {

    @Rule
    TemporaryFolder tmpProjectDir = new TemporaryFolder()

    Project project

    protected IocContext contextSpy

    //TODO: There is a regression in 2.14.1 with API jar regeneration for every test - https://discuss.gradle.org/t/performance-regression-in-projectbuilder-in-2-14-and-3-0/18956
    //https://github.com/gradle/gradle/commit/3216f07b3acb4cbbb8241d8a1d50b8db9940f37e
    def setup() {
        project = ProjectBuilder.builder().withProjectDir(tmpProjectDir.root).build()
        project.apply(plugin: CDeliveryBoyPlugin)

        createRealContextSpyReplaceInPluginAndSetFieldInTest()

        configureDryRunAndCreateRequiredTasks()
        configurePasswordEnvironmentValidation()
    }

    private IocContext createRealContextSpyReplaceInPluginAndSetFieldInTest() {
        return getCDeliveryBoyPluginInstance().enhanceOrReplaceContextHackForIntegrationTesting { IocContext contextInPlugin ->
            contextSpy = (ManualIocContext) Spy(contextInPlugin) //casting to instruct Spock to create spy for ManualIoCContext not just IocContext interface
            return contextSpy
        }
    }

    private void configureDryRunAndCreateRequiredTasks() {
        CDeliveryBoyPluginConfig deliveryBoyConfig = getDeliveryBoyConfig()
        deliveryBoyConfig.dryRun = true
        createAllDependantTasks(new DryRunTaskConfig())
    }

    private void configurePasswordEnvironmentValidation() {
        CiVariablesValidator ciVariablesValidatorAllOkStub = Stub()
        contextSpy.getCiVariablesValidator() >> ciVariablesValidatorAllOkStub
    }

    private CDeliveryBoyPlugin getCDeliveryBoyPluginInstance() {
        return project.plugins.getPlugin(CDeliveryBoyPlugin)
    }
}
