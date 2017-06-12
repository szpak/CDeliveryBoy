package info.solidsoft.gradle.cdeliveryboy

import com.github.zafarkhaja.semver.Version
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesValidator
import org.gradle.testfixtures.ProjectBuilder
import pl.allegro.tech.build.axion.release.domain.VersionConfig
import pl.allegro.tech.build.axion.release.domain.VersionIncrementerContext
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition
import spock.lang.PendingFeature

class OverriddenVersionITSpec extends BasicProjectBuilderITSpec {

    private VersionIncrementerContext versionIncrementerContextStub = Stub()

    def setup() {
        //Override project from BasicProjectBuilderSpec as extra properties (such as "cDeliveryBoy.disablePluginsAutoConfig") cannot be removed
        project = ProjectBuilder.builder().withProjectDir(tmpProjectDir.root).build()
        project.apply(plugin: CDeliveryBoyPlugin)
        project.gradle.startParameter.taskNames = ["prepareForCiBuild"]

        CDeliveryBoyPluginConfig deliveryBoyConfig = getDeliveryBoyConfig()
        deliveryBoyConfig.dryRun = true

        CiVariablesValidator ciVariablesValidatorAllOkStub = Stub()
        project.plugins.getPlugin(CDeliveryBoyPlugin).ciVariablesValidatorIntegrationTestingHack = ciVariablesValidatorAllOkStub

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

    @PendingFeature
    def "should increase configured version number"() {
    }

    @PendingFeature
    def "should use direct release version configured in commit message"() {
    }

    @PendingFeature
    def "should use version number incrementer configured in commit message"() {
    }

    //TODO: Move to a separate trait?
    private VersionConfig getAxionConfiguration() {
        project.getExtensions().getByType(VersionConfig)
    }
}
