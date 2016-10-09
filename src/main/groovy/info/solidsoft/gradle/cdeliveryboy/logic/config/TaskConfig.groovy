package info.solidsoft.gradle.cdeliveryboy.logic.config

//Would be good to have Optional here if limited to Java 8
interface TaskConfig {

    //TODO: Is a single task enough (an artificial umbrella task has to be created at the client side to have multiple tasks in a given step, especially for "build")?
    //TODO: How to name it? createReleaseName? createRelease? createReleaseTasks/Names?
    String getCreateReleaseTask()
    String getBuildProjectTask()
    String getUploadArchivesTask()
    String getCloseRepositoryTask()
    String getPushReleaseTask()
    String getPromoteRepositoryTask()
    String getDropRepositoryTask()
}
