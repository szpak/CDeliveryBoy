package info.solidsoft.gradle.cdeliveryboy.infra.task

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task

import static info.solidsoft.gradle.cdeliveryboy.CDeliveryBoyPluginConstants.RELEASE_TASKS_GROUP_NAME

@CompileStatic
class TaskCreator {

    private final Project project

    TaskCreator(Project project) {
        this.project = project
    }

    @SuppressWarnings("GrUnnecessaryPublicModifier")
    public <T extends Task> T task(String name, Class<T> type, String description, Action<T> configure = {}) {
        return project.tasks.create(name, type) { T task ->
            task.description = description
            task.group = RELEASE_TASKS_GROUP_NAME
            configure.execute(task)
        }
    }
}
