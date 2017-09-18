package info.solidsoft.gradle.cdeliveryboy

import info.solidsoft.gradle.cdeliveryboy.fixture.WithGradleVersionsDeterminer
import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables

class NotInReleaseModeFuncSpec extends BaseTestKitFuncSpec implements WithProjectInExternalGitRepo, WithGradleVersionsDeterminer {

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    def "finish build successfully and display missed conditions on 'prepareForCiBuild' performed in non release mode with Gradle '#gradleVersionToTest'"() {
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

    def "finish build successfully and display missed conditions on 'ciBuild' performed in non release mode with Gradle '#gradleVersionToTest'"() {
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

    def "override configured plugin configuration from command line"() {
        given:
            buildFile << """
                prepareForCiBuild.doFirst {
                    println "cDeliveryBoy.dryRun: " + cDeliveryBoy.dryRun
                    println "cDeliveryBoy.dryRunForceNonSnapshotVersion: " + cDeliveryBoy.dryRunForceNonSnapshotVersion
                }
            """.stripIndent()
        and:
            prepareNonReleasingTravisEnvironmentVariables()
        when:
            //Write code and assertion generation for if more parameters is available
            BuildResult result = runTasks('prepareForCiBuild', '-PcDeliveryBoy.dryRun', '-PcDeliveryBoy.dryRunForceNonSnapshotVersion')
        then:
            verifyAll {
                result.output.contains("cDeliveryBoy.dryRun: true")
                result.output.contains("cDeliveryBoy.dryRunForceNonSnapshotVersion: true")
            }
    }

    private void prepareNonReleasingTravisEnvironmentVariables() {
        environmentVariables.set("TRAVIS_PULL_REQUEST", "false")
        environmentVariables.set("TRAVIS_BRANCH", "release")
        environmentVariables.set("TRAVIS_COMMIT_MSG", "Dummy commit")
        environmentVariables.set("TRAVIS_REPO_SLUG", "foo/bar")
    }
}
