package info.solidsoft.gradle.cdeliveryboy.logic.config

import groovy.transform.CompileStatic

@CompileStatic
class DryRunTaskConfig implements TaskConfig {

    final String createReleaseTask = "fakeCreateRelease"
    final String buildProjectTask = "fakeBuild"
    final String uploadArchivesTask = "fakeUploadArchives"
    final String closeRepositoryTask = "fakeCloseRepository"
    final String pushReleaseTask = "fakePushRelease"
    final String promoteRepositoryTask = "fakePromoteRepository"
    final String dropRepositoryTask = "fakeDropRepository"
}
