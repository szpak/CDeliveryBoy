package info.solidsoft.gradle.cdeliveryboy.logic.config

import groovy.transform.CompileStatic

@CompileStatic
class DefaultTaskConfig implements TaskConfig {

    String createReleaseTask = "createRelease"
    String buildProjectTask = "build"
    String uploadArchivesTask = "uploadArchives"
    String closeRepositoryTask = "closeRepository"
    String pushReleaseTask = "pushRelease2"    //TODO: Switch back to "pushRelease" when Axion is fixed
    String promoteRepositoryTask = null   //"promoteRepository"
    String dropRepositoryTask = null  //TODO: Currently not available in nexus-staging-plugin, switch to "dropRepository" when available - https://github.com/Codearte/gradle-nexus-staging-plugin/issues/17
}
