package info.solidsoft.gradle.cdeliveryboy.logic.config

import info.solidsoft.gradle.cdeliveryboy.infra.PropertyReader
import info.solidsoft.gradle.cdeliveryboy.logic.exception.MissingRequiredCiVariableException
import spock.lang.Specification
import spock.lang.Subject

class CiVariablesValidatorSpec extends Specification {

    private static final String VAR_NAME_1 = "VAR_NAME_1"
    private static final String VAR_NAME_2 = "VAR_NAME_2"

    private PropertyReader propertyReader = Mock()
    private CiVariablesConfig variablesConfig = Mock() {
        getAllVariableNames() >> [VAR_NAME_1, VAR_NAME_2]
    }

    @Subject
    private CiVariablesValidator validator = new CiVariablesValidator(propertyReader, variablesConfig)

    def "should not fail when all variables are available"() {
        given:
            propertyReader.findByName(VAR_NAME_1) >> "OK"
            propertyReader.findByName(VAR_NAME_2) >> "OK"
        when:
            validator.checkExistence()
        then:
            noExceptionThrown()
    }

    def "should fail with meaningful error message on missing variable (#var1Value, #var2Value)"() {
        given:
            propertyReader.findByName(VAR_NAME_1) >> var1Value
            propertyReader.findByName(VAR_NAME_2) >> var2Value
        when:
            validator.checkExistence()
        then:
            MissingRequiredCiVariableException e = thrown()
            e.message.contains(missingVarName)
        and:
            e.missingVariableNames.size() == 1
            e.missingVariableNames[0] == missingVarName
        where:
            var1Value | var2Value | missingVarName
            "OK" | null | VAR_NAME_2
            null | "OK" | VAR_NAME_1

    }

    def "should report all missing variables"() {
        given:
            propertyReader.findByName(VAR_NAME_1) >> null
            propertyReader.findByName(VAR_NAME_2) >> null
        when:
            validator.checkExistence()
        then:
            MissingRequiredCiVariableException e = thrown()
            e.message.contains(VAR_NAME_1)
            e.message.contains(VAR_NAME_2)
        and:
            e.missingVariableNames.containsAll([VAR_NAME_1, VAR_NAME_2])
    }
}
