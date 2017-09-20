package info.solidsoft.gradle.cdeliveryboy

import org.gradle.testkit.runner.BuildResult

class InitializationFuncSpec extends BaseTestKitFuncSpec {

    def "initialize and provide 2 main tasks"() {
        given:
            clearTravisEnvironmentVariables()
        when:
            BuildResult result = runTasks('tasks')
        then:
            result.output.contains("prepareForCiBuild - Prepares for CI build with optional release")
            result.output.contains("ciBuild - Performs CI build with optional release")
    }

    def "initialize and fail on missing Travis configuration environment"() {
        given:
            clearTravisEnvironmentVariables()
        when:
            BuildResult result = runTasksAndFail('prepareForCiBuild')
        then:
            result.output.contains("Missing CI variables: 4 - [TRAVIS_PULL_REQUEST, TRAVIS_BRANCH, TRAVIS_COMMIT_MSG, TRAVIS_REPO_SLUG]")
    }
}
