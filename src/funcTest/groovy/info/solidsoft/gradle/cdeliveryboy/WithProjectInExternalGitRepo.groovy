package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.SelfType
import org.ajoberstar.grgit.Grgit

@SelfType(BaseTestKitFuncSpec)
trait WithProjectInExternalGitRepo {

    void setup() {
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
