package info.solidsoft.gradle.cdeliveryboy

import info.solidsoft.gradle.cdeliveryboy.logic.exception.MissingRequiredCiVariableException
import org.gradle.api.ProjectConfigurationException
import spock.util.Exceptions

class PluginEnvValidationITSpec extends BasicProjectBuilderITSpec {

    def setup() {
        project.gradle.startParameter.taskNames = ["prepareForCiBuild"]

        //Hacky and fragile - it would be good to be able to reset stubbing for CiVariablesValidator mock or override stubbing for context
        contextSpy.getCiVariablesValidator().checkExistence() >> { throw new MissingRequiredCiVariableException(["MISSING_NAME_1"]) }
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
