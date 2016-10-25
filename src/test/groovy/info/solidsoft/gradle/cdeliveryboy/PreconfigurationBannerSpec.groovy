package info.solidsoft.gradle.cdeliveryboy

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import pl.allegro.tech.build.axion.release.domain.VersionConfig
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition
import spock.lang.Specification

class PreconfigurationBannerSpec extends Specification implements ProjectAware, TaskFixtureTrait {

    @Rule
    public TemporaryFolder tmpProjectDir = new TemporaryFolder()

    Project project

    def setup() {
        project = ProjectBuilder.builder().withProjectDir(tmpProjectDir.root).build()
        project.apply(plugin: CDeliveryBoyPlugin)
    }

    def "should add powered by banner in default configuration"() {
        given:
            VersionConfig scmVersion = getAxionConfiguration()
        and:
            String version = "7.1"
            ScmPosition position = Stub()
        when:
            triggerEvaluate()
        then:
            String message = scmVersion.releaseCommitMessage(version, position)
            message.contains("CDeliveryBoy")
    }

    def "should not add powered by banner in explicitly disabled in configuration"() {
        given:
            VersionConfig scmVersion = getAxionConfiguration()
        and:
            String version = "7.1"
            ScmPosition position = Stub()
        and:
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