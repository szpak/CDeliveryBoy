package info.solidsoft.gradle.cdeliveryboy.infra.task

import groovy.transform.CompileStatic
import groovy.transform.PackageScope
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.tooling.BuildException

@CompileStatic
@PackageScope
abstract class AbstractCiTaskOrchestrator {

    protected final Project project

    protected AbstractCiTaskOrchestrator(Project project) {
        this.project = project
    }

    protected boolean isGivenTaskExpectedToBeExecuted(Task taskToChecked) {
        //Task graph would be more reliable, but it's available only after afterEvaluate phrase (in addition it's problematic to test with ProjectBuilder)
        //toLowerCase as Gradle permits it when tasks are called from command line
        List<String> requiredTaskNamesAsLowerCase = project.gradle.startParameter.taskNames.collect { it.toLowerCase() }
        return requiredTaskNamesAsLowerCase.contains(taskToChecked.name.toLowerCase())
    }

    protected Task getJustOneTaskByNameOrFail(String taskName) {
        Set<Task> tasksByName = project.getTasksByName(taskName, false)
        if (tasksByName.size() != 1) {
            throw new BuildException("Expected exactly 1 task with name $taskName. Found ${tasksByName.size()}: '${tasksByName*.name}'", null)
        }
        return tasksByName.first()
    }
}
