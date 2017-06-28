package info.solidsoft.gradle.cdeliveryboy

import com.github.zafarkhaja.semver.Version
import info.solidsoft.gradle.cdeliveryboy.logic.BuildConditionEvaluator
import pl.allegro.tech.build.axion.release.domain.VersionConfig
import pl.allegro.tech.build.axion.release.domain.VersionIncrementerContext
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition
import spock.lang.PendingFeature

import static info.solidsoft.gradle.cdeliveryboy.logic.ForcedVersion.forcedVersionWithValue
import static info.solidsoft.gradle.cdeliveryboy.logic.ForcedVersion.noVersionForced

class OverriddenVersionITSpec extends BasicProjectBuilderITSpec {

    private VersionIncrementerContext versionIncrementerContextStub = Stub()
    private BuildConditionEvaluator buildConditionEvaluatorStub = Stub()

    def setup() {
        project.gradle.startParameter.taskNames = ["prepareForCiBuild"]

        versionIncrementerContextStub.currentVersion >> Version.valueOf("0.5.1")
        versionIncrementerContextStub.scmPosition >> Stub(ScmPosition)

        //TODO: Move to separate fixture in Trait?
        buildConditionEvaluatorStub.forcedVersion() >> noVersionForced()
        buildConditionEvaluatorStub.isReleaseTriggered() >> true
        buildConditionEvaluatorStub.isInReleaseBranch() >> true
        buildConditionEvaluatorStub.isSnapshotVersion() >> true
        project.plugins.getPlugin(CDeliveryBoyPlugin).buildConditionEvaluatorIntegrationTestingHack = buildConditionEvaluatorStub
    }

    def "should increase minor version by default"() {
        expect:
            triggerEvaluateAndReturnReleaseVersion() == "0.6.0"
    }

    def "should allow to override in Axion configuration default version incrementer"() {
        given:
            getAxionConfiguration().versionIncrementer('incrementMajor')
        when:
            String releaseVersion = triggerEvaluateAndReturnReleaseVersion()
        then:
            releaseVersion == "1.0.0"
    }

    @PendingFeature //Cannot be easily implemented as afterEvaluate is executed after Axion configuration is resolved
    def "should increase configured version number"() {}

    def "should use direct release version configured in commit message"() {
        given:
            String forcedVersion = "0.7.7"
        when:
            String releaseVersion = triggerEvaluateAndReturnReleaseVersion()
        then:
            buildConditionEvaluatorStub.forcedVersion() >> forcedVersionWithValue(forcedVersion)    //in "then" to override previous stubbing
            releaseVersion == forcedVersion
    }

    def "should use version number incrementer (#forcedIncrementerName) configured in commit message"() {
        when:
            String releaseVersion = triggerEvaluateAndReturnReleaseVersion()
        then:
            buildConditionEvaluatorStub.forcedVersion() >> forcedVersionWithValue(forcedIncrementerName)    //in "then" to override previous stubbing
            versionIncrementerContextStub.currentVersion >> Version.valueOf("0.5.1-beta1")
        and:
            releaseVersion == expectedVersion
        where:
            forcedIncrementerName || expectedVersion
            "MAJOR"               || "1.0.0"
            "MINOR"               || "0.6.0"
            "PATCH"               || "0.5.2"
            "PRERELEASE"          || "0.5.1-beta2"
    }

    private String triggerEvaluateAndReturnReleaseVersion() {
        triggerEvaluate()
        //and
        Closure versionIncrementer = getAxionConfiguration().versionIncrementer
        String releaseVersion = versionIncrementer.call(versionIncrementerContextStub)
        return releaseVersion
    }

    //TODO: Move to a separate trait?
    private VersionConfig getAxionConfiguration() {
        return project.getExtensions().getByType(VersionConfig)
    }
}
