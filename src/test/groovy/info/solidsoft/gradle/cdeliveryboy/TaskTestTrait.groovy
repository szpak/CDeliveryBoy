package info.solidsoft.gradle.cdeliveryboy

import info.solidsoft.gradle.cdeliveryboy.logic.config.TaskConfig

//@SelfType(ProjectAware)   //TODO: Temporary disabled due to: https://youtrack.jetbrains.com/issue/IDEA-161993
//@CompileStatic
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