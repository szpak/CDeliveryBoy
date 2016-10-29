package info.solidsoft.gradle.cdeliveryboy.logic

import info.solidsoft.gradle.cdeliveryboy.infra.PropertyReader
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.ProjectConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.TriggerConfig
import spock.lang.Specification
import spock.lang.Subject

@SuppressWarnings("GroovyPointlessBoolean")
class BuildConditionEvaluatorSpec extends Specification {

    private static final String TEST_CI_COMMIT_MESSAGE_VARIABLE_NAME = "TEST_COMMIT_MSG"
    public static final String TEST_RELEASE_COMMAND = "TEST_RELEASE_COMMAND"
    public static final String NOT_TRIGGERING_COMMIT_MESSAGE = "any commit message"
    public static final String TRIGGERING_COMMIT_MESSAGE = TEST_RELEASE_COMMAND + " and more"
    public static final String TEST_SKIP_RELEASE_VARIABLE_NAME = "TEST_SKIP_RELEASE"

    private CiVariablesConfig ciVariablesConfig = Stub()
    private CDeliveryBoyPluginConfig pluginConfig = Stub()
    private PropertyReader environmentVariableReader = Stub()
    private ProjectConfig projectConfig = Stub()

    private TriggerConfig trigger = Stub()

    @Subject
    private BuildConditionEvaluator buildConditionEvaluator

    //TODO: How to test it in a sensible way? Prefabricated dependencies from a test data builder?
    void setup() {
        buildConditionEvaluator = new BuildConditionEvaluator(ciVariablesConfig, pluginConfig, environmentVariableReader, projectConfig)
        //and
        pluginConfig.trigger >> trigger
        trigger.onDemandReleaseTriggerCommand >> TEST_RELEASE_COMMAND
        trigger.skipReleaseVariableName >> TEST_SKIP_RELEASE_VARIABLE_NAME
        ciVariablesConfig.commitMessageName >> TEST_CI_COMMIT_MESSAGE_VARIABLE_NAME
    }

    def "should ignore commit message if releaseOnDemand is disabled"() {
        given:
            environmentVariableReader.findByName(TEST_CI_COMMIT_MESSAGE_VARIABLE_NAME) >> NOT_TRIGGERING_COMMIT_MESSAGE
            environmentVariableReader.findByName(TEST_SKIP_RELEASE_VARIABLE_NAME) >> null
        and:
            trigger.releaseOnDemand >> false
        expect:
            buildConditionEvaluator.releaseTriggered
    }

    def "releaseTriggered (#expectedReleaseTriggered) should depend on commit message if releaseOnDemand is enabled"() {
        given:
            environmentVariableReader.findByName(TEST_CI_COMMIT_MESSAGE_VARIABLE_NAME) >> commitMessage
            environmentVariableReader.findByName(TEST_SKIP_RELEASE_VARIABLE_NAME) >> null
        and:
            trigger.releaseOnDemand >> true
        expect:
            buildConditionEvaluator.releaseTriggered == expectedReleaseTriggered
        where:
            commitMessage                 || expectedReleaseTriggered
            NOT_TRIGGERING_COMMIT_MESSAGE || false
            TRIGGERING_COMMIT_MESSAGE     || true
    }

    def "releaseTriggered (#expectedReleaseTriggered) should depend on SKIP_RELEASE (#skipReleaseValue) if releaseOnDemand disabled"() {
        given:
            environmentVariableReader.findByName(TEST_CI_COMMIT_MESSAGE_VARIABLE_NAME) >> NOT_TRIGGERING_COMMIT_MESSAGE
            trigger.releaseOnDemand >> false
        and:
            environmentVariableReader.findByName(TEST_SKIP_RELEASE_VARIABLE_NAME) >> skipReleaseValue
        expect:
            buildConditionEvaluator.releaseTriggered == expectedReleaseTriggered
        where:
            skipReleaseValue || expectedReleaseTriggered
            "true"           || false
            "false"          || true
            null             || true
    }

    def "releaseTriggered (#expectedReleaseTriggered) should depend on SKIP_RELEASE (#skipReleaseValue) if releaseOnDemand enabled and fulfilled"() {
        given:
            environmentVariableReader.findByName(TEST_CI_COMMIT_MESSAGE_VARIABLE_NAME) >> TEST_RELEASE_COMMAND
            trigger.releaseOnDemand >> true
        and:
            environmentVariableReader.findByName(TEST_SKIP_RELEASE_VARIABLE_NAME) >> skipReleaseValue
        expect:
            buildConditionEvaluator.releaseTriggered == expectedReleaseTriggered
        where:
            skipReleaseValue || expectedReleaseTriggered
            "true"           || false
            "false"          || true
            null             || true
    }
}
