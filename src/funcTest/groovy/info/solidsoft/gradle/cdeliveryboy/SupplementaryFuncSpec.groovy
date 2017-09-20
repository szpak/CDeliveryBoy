package info.solidsoft.gradle.cdeliveryboy

import org.gradle.testkit.runner.BuildResult

class SupplementaryFuncSpec extends BaseTestKitFuncSpec implements WithProjectInExternalGitRepo {

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
}
