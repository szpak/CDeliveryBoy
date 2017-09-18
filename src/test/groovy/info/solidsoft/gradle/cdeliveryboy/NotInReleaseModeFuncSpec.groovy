package info.solidsoft.gradle.cdeliveryboy

import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables

class NotInReleaseModeFuncSpec extends BaseTestKitFuncSpec implements WithProjectInExternalGitRepo {

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    def "finish build successfully and display missed conditions on '#ciTask' performed in non release mode"() {
        given:
            prepareNonReleasingTravisEnvironmentVariables()
        when:
            BuildResult result = runTasks(ciTask)
        then:
            result.output.contains("✘ IN RELEASE MODE")
            result.tasks.collect { it.path }.containsAll(expectedTasksToExecute)
        where:
            ciTask              || expectedTasksToExecute
            "prepareForCiBuild" || []
            "ciBuild"           || [":test", ":build"]
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
