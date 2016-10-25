package info.solidsoft.gradle.cdeliveryboy

import pl.allegro.tech.build.axion.release.domain.VersionConfig
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition

class PreconfigurationBannerSpec extends BasicProjectBuilderSpec {

    private String version
    private ScmPosition position
    private VersionConfig scmVersion

    def setup() {
        project.apply(plugin: CDeliveryBoyPlugin)

        version = "7.1"
        position = Stub()
        scmVersion = getAxionConfiguration()
    }

    def "should add powered by banner in default configuration"() {
        when:
            triggerEvaluate()
        then:
            String message = scmVersion.releaseCommitMessage(version, position)
            message.contains("CDeliveryBoy")
    }

    def "should not add powered by banner in explicitly disabled in configuration"() {
        given:
            getDeliveryBoyConfig().git.disablePoweredByMessage = true
        when:
            triggerEvaluate()
        then:
            String message = scmVersion.releaseCommitMessage(version, position)
            !message.contains("CDeliveryBoy")
    }

    private VersionConfig getAxionConfiguration() {
        project.getExtensions().getByType(VersionConfig)
    }
}