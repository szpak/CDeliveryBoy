package info.solidsoft.gradle.cdeliveryboy.logic

import info.solidsoft.gradle.cdeliveryboy.infra.OverriddenVersionInCommitMessageFinder
import info.solidsoft.gradle.cdeliveryboy.infra.PropertyReader
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.ProjectConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.TriggerConfig
import spock.lang.Specification
import spock.lang.Subject

import static OverriddenVersion.overriddenVersionWithValue
import static OverriddenVersion.noVersionOverridden


@SuppressWarnings("GroovyPointlessBoolean")
class BuildConditionEvaluatorSpec extends Specification {

    private static final String TEST_CI_COMMIT_MESSAGE_VARIABLE_NAME = "TEST_COMMIT_MSG"
    private static final String TEST_RELEASE_COMMAND = "[#TEST_DO_RELEASE]"
    private static final String NOT_TRIGGERING_COMMIT_MESSAGE = "any commit message"
    private static final String TRIGGERING_COMMIT_MESSAGE = TEST_RELEASE_COMMAND + " and more"
    private static final String TRIGGERING_COMMIT_MESSAGE_WITH_VERSION = TEST_RELEASE_COMMAND + " [#0.5.0-over] and more"
    private static final String TEST_SKIP_RELEASE_VARIABLE_NAME = "TEST_SKIP_RELEASE"
    private static final String SOME_COMMIT_MESSAGE = "anyCommitMessage"
    private static final String TEST_CI_BRANCH_NAME = "BRANCH_NAME_ENV"
    private static final String TEST_CI_IS_PR_NAME = "IS_PR_ENV"

    private CiVariablesConfig ciVariablesConfig = Stub()
    private CDeliveryBoyPluginConfig pluginConfig = Stub()
    private PropertyReader environmentVariableReader = Stub()
    private ProjectConfig projectConfig = Stub()
    private OverriddenVersionInCommitMessageFinder overriddenVersionDeterminer = Stub()

    private TriggerConfig trigger = Stub()

    @Subject
    private BuildConditionEvaluator buildConditionEvaluator

    //TODO: How to test it in a sensible way? Prefabricated dependencies from a test data builder?
    void setup() {
        buildConditionEvaluator = new BuildConditionEvaluator(ciVariablesConfig, pluginConfig, environmentVariableReader, projectConfig,
                overriddenVersionDeterminer)
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

    def "should get override version for commit message if provided (#overriddenVersion)"() {
        given:
            environmentVariableReader.findByName(TEST_CI_COMMIT_MESSAGE_VARIABLE_NAME) >> SOME_COMMIT_MESSAGE
            overriddenVersionDeterminer.findOverriddenVersionInCommitMessageIfProvided(SOME_COMMIT_MESSAGE) >> overriddenVersion
        expect:
            buildConditionEvaluator.overriddenVersion() == overriddenVersion
        where:
            overriddenVersion << [overriddenVersionWithValue("0.5.0"), noVersionOverridden()]
    }

    def "should display conditions in human-friendly way"() {
        given:
            ciVariablesConfig.branchNameName >> TEST_CI_BRANCH_NAME
            environmentVariableReader.findByName(TEST_CI_BRANCH_NAME) >> "master"
            ciVariablesConfig.isPrName >> TEST_CI_IS_PR_NAME
            environmentVariableReader.findByName(TEST_CI_IS_PR_NAME) >> false
            trigger.releaseOnDemand >> true
            projectConfig.version >> "0.3.2-SNAPSHOT"
            environmentVariableReader.findByName(TEST_CI_COMMIT_MESSAGE_VARIABLE_NAME) >> TRIGGERING_COMMIT_MESSAGE_WITH_VERSION
            overriddenVersionDeterminer.findOverriddenVersionInCommitMessageIfProvided(TRIGGERING_COMMIT_MESSAGE_WITH_VERSION) >>
                    overriddenVersionWithValue("0.5.0-over")
        expect:
            buildConditionEvaluator.getReleaseConditionsAsString() == """
In release mode: true
  - in release branch: true
    - configured: 'master', actual: 'master'
    - not PR build: true
  - release triggered: true
    - not skipped by env variable ('TEST_SKIP_RELEASE'): true
    - release on demand required: true
    - release on demand triggered ('[#TEST_DO_RELEASE]'): true
  - is SNAPSHOT: true
  - current version: '0.3.2-SNAPSHOT'
  - overridden version: '0.5.0-over'
"""
    }
}
