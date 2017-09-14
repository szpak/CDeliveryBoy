package info.solidsoft.gradle.cdeliveryboy

import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesValidator
import info.solidsoft.gradle.cdeliveryboy.logic.exception.MissingRequiredCiVariableException
import org.gradle.api.ProjectConfigurationException
import spock.util.Exceptions

import static org.mockito.BDDMockito.given

class PluginEnvValidationITSpec extends BasicProjectBuilderITSpec {

    def setup() {
        project.gradle.startParameter.taskNames = ["prepareForCiBuild"]

        CiVariablesValidator ciVariablesValidatorStub = Stub() {
            checkExistence() >> { throw new MissingRequiredCiVariableException(["MISSING_NAME_1"]) }
        }
        given(contextSpy.getCiVariablesValidator()).willReturn(ciVariablesValidatorStub)
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
