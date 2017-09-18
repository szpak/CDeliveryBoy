package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.CompileStatic
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.util.GradleVersion
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestName
import spock.lang.Specification

@CompileStatic
class BaseTestKitFuncSpec extends Specification implements WithNebulaTestGoodies {

    @Rule
    TestName testName = new TestName()

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder()

    protected File projectDir
    protected File buildFile
    protected File settingsFile

    protected boolean debug
    protected String gradleVersion = GradleVersion.current().version
    protected boolean forwardOutput

    def setup() {
        configureProjectDirInBuildReplaceIfExists()
        buildFile = new File(projectDir, "build.gradle")
        settingsFile = new File(projectDir, "settings.gradle")

        buildFile << basicBuildFile()
    }

    private void configureProjectDirInBuildReplaceIfExists() {
        projectDir = new File(new File(temporaryFolder.root, this.class.canonicalName), testName.methodName.replaceAll(/\W+/, '-')).absoluteFile
        projectDir.mkdirs()
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
        BuildResult buildResult = prepareGenericGradleRunner(tasks).build()
        return checkForDeprecations(buildResult)
    }

    private GradleRunner prepareGenericGradleRunner(String... tasks) {
        GradleRunner runner = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(tasks + "-i")
                .withDebug(debug)
                .withPluginClasspath()
                .withGradleVersion(gradleVersion)
        if (forwardOutput) {
            runner.forwardOutput()
        }
        return runner
    }

    protected BuildResult runTasksAndFail(String... tasks) {
        BuildResult buildResult = prepareGenericGradleRunner(tasks).buildAndFail()
        return checkForDeprecations(buildResult)
    }

    protected BuildResult checkForDeprecations(BuildResult result) {
        checkForDeprecationsInOutput(result.output)
        return result
    }
}
