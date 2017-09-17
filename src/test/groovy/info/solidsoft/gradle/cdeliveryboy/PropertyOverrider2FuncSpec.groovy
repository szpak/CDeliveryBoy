package info.solidsoft.gradle.cdeliveryboy

import nebula.test.IntegrationTestKitSpec
import org.gradle.testkit.runner.BuildResult
import org.junit.Rule
import org.junit.contrib.java.lang.system.EnvironmentVariables
import org.junit.rules.TemporaryFolder

class PropertyOverrider2FuncSpec extends IntegrationTestKitSpec implements WithProjectInExternalGitRepo {

    //Cannot be moved to WithProjectInExternalGitRepo as there is problem with rules in Groovy traits
    @Rule
    public final TemporaryFolder tmpOutsideFolderDir = new TemporaryFolder()

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    def "finish build successfully and display missed conditions on 'prepareCiBuild' performed in non release mode"() {
        given:
            prepareNonReleasingTravisEnvironmentVariables()
        and:
            buildFile << """
                plugins {
                    id 'info.solidsoft.cdeliveryboy'
                }
                apply plugin: 'java'
                version = "0.1.0-SNAPSHOT"
            """.stripIndent()
        and:
            writeHelloWorld('gradle.cdeliveryboy.test.hello')
        when:
            BuildResult result = runTasks('prepareForCiBuild')
        then:
            result.output.contains("✘ IN RELEASE MODE")
    }

    def "override configured plugin configuration from command line"() {
        given:
            buildFile << """
                plugins {
                    id 'info.solidsoft.cdeliveryboy'
                }
                apply plugin: 'java'
                version = "0.1.0-SNAPSHOT"
                
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

    def "finish build successfully and display missed conditions on 'ciBuild' performed in non release mode"() {
        given:
            prepareNonReleasingTravisEnvironmentVariables()
        and:
            buildFile << """
                plugins {
                    id 'info.solidsoft.cdeliveryboy'
                }
                apply plugin: 'java'
                version = "0.1.0-SNAPSHOT"
            """.stripIndent()
        and:
            writeHelloWorld('gradle.cdeliveryboy.test.hello')
        when:
            BuildResult result = runTasks('ciBuild')
        then:
            result.output.contains("✘ IN RELEASE MODE")
            result.tasks.collect { it.path }.containsAll(":test", ":build")
    }

    private void prepareNonReleasingTravisEnvironmentVariables() {
        environmentVariables.set("TRAVIS_PULL_REQUEST", "false")
        environmentVariables.set("TRAVIS_BRANCH", "release")
        environmentVariables.set("TRAVIS_COMMIT_MSG", "Dummy commit")
        environmentVariables.set("TRAVIS_REPO_SLUG", "foo/bar")
    }
}
