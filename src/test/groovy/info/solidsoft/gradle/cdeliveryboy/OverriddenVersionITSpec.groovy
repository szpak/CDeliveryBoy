package info.solidsoft.gradle.cdeliveryboy

import com.github.zafarkhaja.semver.Version
import info.solidsoft.gradle.cdeliveryboy.logic.BuildConditionEvaluator
import pl.allegro.tech.build.axion.release.domain.VersionConfig
import pl.allegro.tech.build.axion.release.domain.VersionIncrementerContext
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition
import spock.lang.PendingFeature

import static info.solidsoft.gradle.cdeliveryboy.logic.ForcedVersion.forcedVersionWithValue

class OverriddenVersionITSpec extends BasicProjectBuilderITSpec {

    private VersionIncrementerContext versionIncrementerContextStub = Stub()

    def setup() {
        project.gradle.startParameter.taskNames = ["prepareForCiBuild"]

        versionIncrementerContextStub.currentVersion >> Version.valueOf("0.5.1")
        versionIncrementerContextStub.scmPosition >> Stub(ScmPosition)
    }

    def "should increase minor version by default"() {
        given:
            triggerEvaluate()
            Closure versionIncrementer = getAxionConfiguration().versionIncrementer
        when:
            String releaseVersion = versionIncrementer.call(versionIncrementerContextStub)
        then:
            releaseVersion == "0.6.0"
    }

    def "should allow to override in Axion configuration default version incrementer"() {
        given:
            getAxionConfiguration().versionIncrementer('incrementMajor')
        and:
            triggerEvaluate()
            Closure versionIncrementer = getAxionConfiguration().versionIncrementer
        when:
            String releaseVersion = versionIncrementer.call(versionIncrementerContextStub)
        then:
            releaseVersion == "1.0.0"
    }

    @PendingFeature //Cannot be easily implemented as afterEvaluate is executed after Axion configuration is resolved
    def "should increase configured version number"() {}

    def "should use direct release version configured in commit message"() {
        given:
            String forcedVersion = "0.7.7"
        and:
            BuildConditionEvaluator buildConditionEvaluatorStub = Stub()
            buildConditionEvaluatorStub.forcedVersion() >> forcedVersionWithValue(forcedVersion)
            project.plugins.getPlugin(CDeliveryBoyPlugin).buildConditionEvaluatorIntegrationTestingHack = buildConditionEvaluatorStub
        and:
            triggerEvaluate()   //TODO: Extract those lines to asserting method
            Closure versionIncrementer = getAxionConfiguration().versionIncrementer
        when:
            String releaseVersion = versionIncrementer.call(versionIncrementerContextStub)
        then:
            releaseVersion == forcedVersion
    }

    @PendingFeature
    def "should use version number incrementer configured in commit message"() {
    }

    //TODO: Move to a separate trait?
    private VersionConfig getAxionConfiguration() {
        return project.getExtensions().getByType(VersionConfig)
    }
}
