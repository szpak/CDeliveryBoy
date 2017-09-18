package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.Canonical
import info.solidsoft.gradle.cdeliveryboy.fixture.WithGradleVersionsDeterminer
import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import spock.lang.Shared

class NotInReleaseModeFuncSpec extends BaseTestKitFuncSpec implements WithProjectInExternalGitRepo, WithGradleVersionsDeterminer {

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

    @Shared
    TaskAndVersion tasksAndVersions = generateCiTasksAndGradleVersionsCombinations()

    def "finish build successfully and display missed conditions on '#ciTask' performed in non release mode with Gradle '#gradleVersionToTest'"() {
        given:
            prepareNonReleasingTravisEnvironmentVariables()
        and:
            gradleVersion = gradleVersionToTest
        when:
            BuildResult result = runTasks(ciTask)
        then:
            result.output.contains("✘ IN RELEASE MODE")
        where:
            ciTask << tasksAndVersions.taskNames
            gradleVersionToTest << tasksAndVersions.versions
    }

    private TaskAndVersion generateCiTasksAndGradleVersionsCombinations() {
        List<String> expectedTasksToExecute = ["prepareForCiBuild", "ciBuild"]
        List<String> versionsToExecute = determineGradleVersionsToTest()

        List<List<String>> combinations = GroovyCollections.combinations([expectedTasksToExecute, versionsToExecute] as Iterable)

        TaskAndVersion tasksAndVersions = new TaskAndVersion(combinations*.get(0), combinations*.get(1))

        assert tasksAndVersions.taskNames.containsAll(expectedTasksToExecute)
        assert tasksAndVersions.versions.containsAll(versionsToExecute)

        return tasksAndVersions
    }

    @Canonical
    private static class TaskAndVersion {
        List<String> taskNames
        List<String> versions
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
