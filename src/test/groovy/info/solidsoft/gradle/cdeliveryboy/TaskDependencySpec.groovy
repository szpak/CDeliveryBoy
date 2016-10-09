package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.NotYetImplemented
import info.solidsoft.gradle.cdeliveryboy.logic.BuildConditionEvaluator
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.DryRunTaskConfig
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.tooling.BuildException
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.util.Exceptions

@SuppressWarnings("GrMethodMayBeStatic")
class TaskDependencySpec extends Specification implements TaskTestTrait, ProjectAware {

    @Rule
    public TemporaryFolder tmpProjectDir = new TemporaryFolder()

    //visibility for TaskTestTrait and ProjectAware
    Project project
    private CDeliveryBoyPluginConfig deliveryBoyConfig

    private BuildConditionEvaluator buildConditionEvaluatorStub

    //TODO: There is a regression in 2.14.1 with API jar regeneration for every test - https://discuss.gradle.org/t/performance-regression-in-projectbuilder-in-2-14-and-3-0/18956
    //https://github.com/gradle/gradle/commit/3216f07b3acb4cbbb8241d8a1d50b8db9940f37e
    def setup() {
        project = ProjectBuilder.builder().withProjectDir(tmpProjectDir.root).build()
        project.gradle.startParameter.taskNames = ["prepareForCiBuild"]
        project.extensions.extraProperties.set("cDeliveryBoy.disablePluginsAutoConfig", "true") //External plugins cannot be used in ProjectBuilder

//        project.apply(plugin: "java") //one second delay - do not apply if not needed in given specification
        project.apply(plugin: CDeliveryBoyPlugin)

        deliveryBoyConfig = getDeliveryBoyConfig(project)
        deliveryBoyConfig.dryRun = true

        createAllDependantTasks(new DryRunTaskConfig())

        buildConditionEvaluatorStub = Stub()
        project.plugins.getPlugin(CDeliveryBoyPlugin).buildConditionEvaluatorIntegrationTestingHack = buildConditionEvaluatorStub

    }

    //TODO: Just to verify task dependencies work in ProjectBuilder - think if those kind of tests should be used to verify "the inter task logic"

    def "should not prepare release if any of isInReleaseBranch (#isInReleaseBranch) or isReleaseTriggered (#isReleaseTriggered) is not fulfilled "() {
        given:
            buildConditionEvaluatorStub.isInReleaseBranch() >> isInReleaseBranch
            buildConditionEvaluatorStub.isReleaseTriggered() >> isReleaseTriggered
            buildConditionEvaluatorStub.isSnapshotVersion() >> true
        and:
            project.tasks.create("currentVersion")
        and:
            Task ciBuildTask = getJustOneTaskByNameOrFail("prepareForCiBuild")
        expect:
            getDependencyNamesForTask(ciBuildTask) == ["currentVersion"] as Set
            ciBuildTask.getMustRunAfter().getDependencies(ciBuildTask) == [] as Set
        where:
            isInReleaseBranch | isReleaseTriggered
            false             | true
            true              | false
    }

    def "should depend on createRelease task if in release branch and release triggered"() {
        given:
            stubForReleaseWithSnapshot()
        when:
            Task ciBuildTask = getJustOneTaskByNameOrFail("prepareForCiBuild")
        then:
            getDependencyNamesForTask(ciBuildTask) == ["fakeCreateRelease"] as Set
    }

    def "should put release tasks in order when in release mode with non snapshot version"() {
        given:
            buildConditionEvaluatorStub.isInReleaseBranch() >> true
            buildConditionEvaluatorStub.isReleaseTriggered() >> true
            buildConditionEvaluatorStub.isSnapshotVersion() >> false
        and:
            project.gradle.startParameter.taskNames = ['ciBuild']
        when:
            triggerEvaluate()
        then:
            verifyAll {
                assertGivenTaskMustRunAfterAnother("fakePromoteRepository", "fakePushRelease")
                assertGivenTaskMustRunAfterAnother("fakeCloseRepository", "fakeUploadArchives")
                assertGivenTaskMustRunAfterAnother("fakePushRelease", "fakeCloseRepository")
            }
    }

    def "should fail with meaningful message if release requested in release mode with snapshot version"() {
        given:
            stubForReleaseWithSnapshot()
        and:
            project.gradle.startParameter.taskNames = ['ciBuild']
        when:
            triggerEvaluate()
        then:
            ProjectConfigurationException e = thrown()
            Throwable rootCause = Exceptions.getRootCause(e)
            rootCause.class == BuildException
            rootCause.message.contains('prepareForCiBuild')
    }

    def "should override static configuration with command line properties"() {
        given:
            stubForReleaseWithSnapshot()
        and:
            project.gradle.startParameter.taskNames = ['prepareForCiBuild']
            project.tasks.create("createRelease")
        and:
            project.extensions.extraProperties.set("cDeliveryBoy.dryRun", "false")
        and:
            Task ciBuildTask = getJustOneTaskByNameOrFail("prepareForCiBuild")
        expect:
            getDependencyNamesForTask(ciBuildTask) == ["createRelease"] as Set
    }

    def "promotion task should be disabled in ciBuild if auto-promotion disabled in configuration"() {
        given:
            buildConditionEvaluatorStub.isInReleaseBranch() >> true
            buildConditionEvaluatorStub.isReleaseTriggered() >> true
            buildConditionEvaluatorStub.isSnapshotVersion() >> false
            project.gradle.startParameter.taskNames = ['ciBuild']
        and:
            deliveryBoyConfig.autoPromote = false
        when:
            Task promoteRepositoryTask = getJustOneTaskByNameOrFail("fakePromoteRepository")
        then:
            !promoteRepositoryTask.enabled
    }

    def "required task names detection should be case insensitive"() {
        given:
            project.gradle.startParameter.taskNames = ["CIbuild"]
        when:
            Task ciBuildTask = getJustOneTaskByNameOrFail("ciBuild")
        then:
            getDependencyNamesForTask(ciBuildTask).contains('fakeBuild')
    }

    def "should allow to change task names in configuration"() {
        given:
            stubForReleaseWithSnapshot()
            project.extensions.extraProperties.set("cDeliveryBoy.dryRun", "false")
            project.gradle.startParameter.taskNames = ['prepareForCiBuild']
            project.tasks.create("newCreateRelease")
        and:
            applyConfiguration(deliveryBoyConfig)
        and:
            Task prepareForCiBuildTask = getJustOneTaskByNameOrFail("prepareForCiBuild")
        expect:
            getDependencyNamesForTask(prepareForCiBuildTask).contains("newCreateRelease")
        where:
            applyConfiguration << [
                    { CDeliveryBoyPluginConfig c -> c.tasks.createReleaseTask = "newCreateRelease" },
                    { CDeliveryBoyPluginConfig c ->
                        c.tasks {
                            createReleaseTask = "newCreateRelease"
                        }
                    }
            ]
    }

    @NotYetImplemented  //Can be tested with ProjectBuilder?
    def "should apply preconfiguration on Axion plugin"() {
    }

    private CDeliveryBoyPluginConfig getDeliveryBoyConfig(Project project) {
        project.getExtensions().getByType(CDeliveryBoyPluginConfig)
    }

    private Task getJustOneTaskByNameOrFail(String taskName) {
        Set<Task> tasks = project.getTasksByName(taskName, false) //forces "afterEvaluate"
        assert tasks?.size() == 1 : "Expected tasks: '$taskName', All tasks: ${project.tasks}"
        return tasks[0]
    }

    private void triggerEvaluate() {
        getJustOneTaskByNameOrFail('tasks')
    }

    private void assertGivenTaskMustRunAfterAnother(String taskName, String predecessorName) {
        //Task graph is not available in ProjectBuilder
        Task task = getJustOneTaskByNameOrFail(taskName)
        Task expectedPredecessor = getJustOneTaskByNameOrFail(predecessorName)
        assert task.mustRunAfter.getDependencies(task).contains(expectedPredecessor)
    }

    private Set<String> getDependencyNamesForTask(Task task) {
        return getDependenciesForTask(task)*.name as Set
    }

    private Set<Task> getDependenciesForTask(Task task) {
        return task.taskDependencies.getDependencies(task)
    }

    private void stubForReleaseWithSnapshot() {
        buildConditionEvaluatorStub.isInReleaseBranch() >> true
        buildConditionEvaluatorStub.isReleaseTriggered() >> true
        buildConditionEvaluatorStub.isSnapshotVersion() >> true
    }
}
