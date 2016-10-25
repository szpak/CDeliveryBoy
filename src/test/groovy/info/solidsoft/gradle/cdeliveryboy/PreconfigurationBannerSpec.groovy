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

    private String version
    private ScmPosition position
    private VersionConfig scmVersion

    def setup() {
        project = ProjectBuilder.builder().withProjectDir(tmpProjectDir.root).build()
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