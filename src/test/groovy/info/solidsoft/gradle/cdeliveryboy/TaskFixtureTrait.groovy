package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.CompileStatic
import groovy.transform.SelfType
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import org.gradle.api.Task

@SelfType(ProjectAware)
@CompileStatic
trait TaskFixtureTrait {

    void triggerEvaluate() {
        getJustOneTaskByNameOrFail('tasks')
    }

    Task getJustOneTaskByNameOrFail(String taskName) {
        Set<Task> tasks = project.getTasksByName(taskName, false) //forces "afterEvaluate"
        assert tasks?.size() == 1 : "Expected tasks: '$taskName', All tasks: ${project.tasks}"
        return tasks[0]
    }

    CDeliveryBoyPluginConfig getDeliveryBoyConfig() {
        project.getExtensions().getByType(CDeliveryBoyPluginConfig)
    }
}
