package info.solidsoft.gradle.cdeliveryboy

import nebula.test.IntegrationTestKitSpec

class BaseTestKitFuncSpec extends IntegrationTestKitSpec {

    def setup() {
        buildFile << basicBuildFile()
    }

    protected String basicBuildFile() {
        return """
                plugins {
                    id 'info.solidsoft.cdeliveryboy'
                }
                apply plugin: 'java'
                version = "0.1.0-SNAPSHOT"
        """.stripIndent()
    }
}
