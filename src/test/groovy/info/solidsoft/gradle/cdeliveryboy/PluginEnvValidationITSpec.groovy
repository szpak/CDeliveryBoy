package info.solidsoft.gradle.cdeliveryboy

import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesValidator
import info.solidsoft.gradle.cdeliveryboy.logic.config.DryRunTaskConfig
import info.solidsoft.gradle.cdeliveryboy.logic.exception.MissingRequiredCiVariableException
import org.gradle.api.ProjectConfigurationException
import spock.util.Exceptions

class PluginEnvValidationITSpec extends BasicProjectBuilderSpec implements TaskTestTrait {

    def setup() {
        project.extensions.extraProperties.set("cDeliveryBoy.disablePluginsAutoConfig", "true") //speed up testing, extra plugins are not needed here
        project.apply(plugin: CDeliveryBoyPlugin)

        project.gradle.startParameter.taskNames = ["prepareForCiBuild"]

        CDeliveryBoyPluginConfig deliveryBoyConfig = getDeliveryBoyConfig()
        deliveryBoyConfig.dryRun = true

        createAllDependantTasks(new DryRunTaskConfig())

        CiVariablesValidator ciVariablesValidatorStub = Stub() {
            checkExistence() >> { throw new MissingRequiredCiVariableException(["MISSING_NAME_1"]) }
        }
        project.plugins.getPlugin(CDeliveryBoyPlugin).ciVariablesValidatorIntegrationTestingHack = ciVariablesValidatorStub
    }

    def "should validate required environment properties if #taskName is to be executed"() {
        given:
            project.gradle.startParameter.taskNames = [taskName]
        when:
            triggerEvaluate()
        then:
            ProjectConfigurationException e = thrown()
            Exceptions.getRootCause(e).class == MissingRequiredCiVariableException
        where:
            taskName << ["prepareForCiBuild", "ciBuild"]
    }

    def "should not validate required environment properties if no release task is executes"() {
        given:
            project.gradle.startParameter.taskNames = ["check"]
        when:
            triggerEvaluate()
        then:
            noExceptionThrown()
    }
}
