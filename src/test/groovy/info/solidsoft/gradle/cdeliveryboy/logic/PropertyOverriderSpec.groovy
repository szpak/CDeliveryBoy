package info.solidsoft.gradle.cdeliveryboy.logic

import info.solidsoft.gradle.cdeliveryboy.infra.ProjectPropertyReader
import spock.lang.Specification

class PropertyOverriderSpec extends Specification {

    private static final String TEST_PROPERTY_NAME = "testProperty"

    private ProjectPropertyReader propertyReader = Stub()
    private PropertyOverrider overrider = new PropertyOverrider(propertyReader)

    def "should apply command line boolean property"() {
    }

    def "get boolean value 'true' from property with value '#propertyStringValue'"() {
        given:
            propertyReader.findByName(TEST_PROPERTY_NAME) >> propertyStringValue
        when:
            boolean readValue = overrider.getBooleanValueFromPropertyOrFallback(TEST_PROPERTY_NAME, !expectedBoolean)
        then:
            readValue == expectedBoolean
        where:
            propertyStringValue || expectedBoolean
            "true"              || true
            "1"                 || true
            ""                  || true
    }

    @SuppressWarnings("GroovyPointlessBoolean")
    def "get boolean value 'false' from property with value '#propertyStringValue'"() {
        given:
            propertyReader.findByName(TEST_PROPERTY_NAME) >> propertyStringValue
        when:
            boolean readValue = overrider.getBooleanValueFromPropertyOrFallback(TEST_PROPERTY_NAME, !expectedBoolean)
        then:
            readValue == expectedBoolean
        where:
            propertyStringValue || expectedBoolean
            "false"             || false
            "0"                 || false
            "foobar"            || false
    }

    def "use fallback boolean value '#fallbackValue' on null"() {
        given:
            propertyReader.findByName(TEST_PROPERTY_NAME) >> null
        when:
            boolean readValue = overrider.getBooleanValueFromPropertyOrFallback(TEST_PROPERTY_NAME, fallbackValue)
        then:
            readValue == fallbackValue
        where:
            fallbackValue << [true, false]
    }

    def "get String value '#propertyStringValue' from property"() {
        given:
            propertyReader.findByName(TEST_PROPERTY_NAME) >> propertyStringValue
        when:
            String readValue = overrider.getStringValueFromPropertyOrFallback(TEST_PROPERTY_NAME, "777")
        then:
            readValue == propertyStringValue
        where:
            propertyStringValue << ["foobar", "", "2", "true"]
    }

    def "use fallback String on null"() {
        given:
            propertyReader.findByName(TEST_PROPERTY_NAME) >> null
        and:
            String fallbackValue = "777"
        when:
            String readValue = overrider.getStringValueFromPropertyOrFallback(TEST_PROPERTY_NAME, fallbackValue)
        then:
            readValue == fallbackValue
    }
}
