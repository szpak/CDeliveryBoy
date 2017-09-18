package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.CompileStatic
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestName
import spock.lang.Specification

@CompileStatic
class BaseTestKitFuncSpec extends Specification {

    @Rule
    TestName testName = new TestName()

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    protected File projectDir
    protected File buildFile
    protected File settingsFile

    protected boolean debug

    def setup() {
        configureProjectDirInBuildReplaceIfExists()
        buildFile = new File(projectDir, "build.gradle")
        settingsFile = new File(projectDir, "settings.gradle")

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

    protected BuildResult runTasks(String... tasks) {
        BuildResult result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(tasks + "-i")
                .withDebug(debug)
                .withPluginClasspath()
                .forwardOutput()
                .build()
        return /*checkForDeprecations*/(result)
    }

    private void configureProjectDirInBuildReplaceIfExists() {
        //Convention taken after nebula-test
        projectDir = new File(new File(temporaryFolder.root, this.class.canonicalName), testName.methodName.replaceAll(/\W+/, '-')).absoluteFile
        projectDir.mkdirs()
    }
}
