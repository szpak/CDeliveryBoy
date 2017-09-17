package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.SelfType
import nebula.test.IntegrationTestKitSpec
import org.ajoberstar.grgit.Grgit

@SelfType(IntegrationTestKitSpec)
trait WithProjectInExternalGitRepo {

    void setup() {
        projectDir = tmpOutsideFolderDir.root
        settingsFile = new File(projectDir, "settings.gradle")
        buildFile = new File(projectDir, "build.gradle")
        //and
        assertInTempDirectory(projectDir)
        //and
        Grgit.init(dir: projectDir.absolutePath)    //needed for release mode reading or modifying Git repository
    }

    void cleanup() {
        assertInTempDirectory(projectDir)
    }

    void assertInTempDirectory(File directory) {
        assert directory.absolutePath.startsWith("/tmp/")  //TODO: Make it OS agnostic
    }
}
