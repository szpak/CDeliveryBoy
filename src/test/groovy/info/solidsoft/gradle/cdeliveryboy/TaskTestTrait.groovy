package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.CompileStatic
import groovy.transform.SelfType
import info.solidsoft.gradle.cdeliveryboy.logic.config.TaskConfig

@SelfType(ProjectAware)
@CompileStatic
trait TaskTestTrait {

    void createAllDependantTasks(TaskConfig taskConfig) {
        //TODO: Temporary hacky implementation
        createTaskIfNameIsNotEmpty(taskConfig.buildProjectTask)
        createTaskIfNameIsNotEmpty(taskConfig.closeRepositoryTask)
        createTaskIfNameIsNotEmpty(taskConfig.dropRepositoryTask)
        createTaskIfNameIsNotEmpty(taskConfig.createReleaseTask)
        createTaskIfNameIsNotEmpty(taskConfig.promoteRepositoryTask)
        createTaskIfNameIsNotEmpty(taskConfig.pushReleaseTask)
        createTaskIfNameIsNotEmpty(taskConfig.uploadArchivesTask)
    }

    private void createTaskIfNameIsNotEmpty(String taskName) {
        project.tasks.create(taskName)  //maybeCreate?
    }

}