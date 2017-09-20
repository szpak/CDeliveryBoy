package info.solidsoft.gradle.cdeliveryboy

import info.solidsoft.gradle.cdeliveryboy.fixture.WithGradleVersionsDeterminer
import info.solidsoft.gradle.cdeliveryboy.fixture.WithProjectInExternalGitRepo
import org.gradle.testkit.runner.BuildResult

class NotInReleaseModeFuncSpec extends BaseTestKitFuncSpec implements WithProjectInExternalGitRepo, WithGradleVersionsDeterminer {

    def "display missed conditions on 'prepareForCiBuild' performed in non release mode with Gradle '#gradleVersionToTest'"() {
        given:
            prepareNonReleasingTravisEnvironmentVariables()
        and:
            gradleVersion = gradleVersionToTest
        when:
            BuildResult result = runTasks("prepareForCiBuild")
        then:
            result.output.contains("✘ IN RELEASE MODE")
        where:
            gradleVersionToTest << determineGradleVersionsToTest()
    }

    def "display missed conditions on 'ciBuild' performed in non release mode with Gradle '#gradleVersionToTest'"() {
        given:
            prepareNonReleasingTravisEnvironmentVariables()
        and:
            gradleVersion = gradleVersionToTest
        when:
            BuildResult result = runTasks("ciBuild")
        then:
            result.output.contains("✘ IN RELEASE MODE")
            result.tasks.collect { it.path }.containsAll([":test", ":build"])
        where:
            gradleVersionToTest << determineGradleVersionsToTest()
    }
}
