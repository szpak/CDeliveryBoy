package info.solidsoft.gradle.cdeliveryboy.logic.config

class GitConfig {

    String releaseBranch = 'master'
    boolean createReleaseCommit = true
    Closure<String> overriddenReleaseCommitMessageCreator = null
    boolean addPoweredByMessage = true
}
