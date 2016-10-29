package info.solidsoft.gradle.cdeliveryboy.logic.config

import groovy.transform.CompileStatic

@CompileStatic
class CDeliveryBoyPluginConfig {

    String ciType = 'travis'

    boolean dryRun = false
    //For dryRun/test purpose
    boolean dryRunForceNonSnapshotVersion = false

    GitConfig git = new GitConfig()
    TriggerConfig trigger = new TriggerConfig()
    NexusConfig nexus = new NexusConfig()
    DefaultTaskConfig tasks = new DefaultTaskConfig()

    void git(@DelegatesTo(GitConfig) Closure closure) {
        git.with(closure)
    }

    void trigger(@DelegatesTo(TriggerConfig) Closure closure) {
        git.with(closure)
    }

    void nexus(@DelegatesTo(NexusConfig) Closure closure) {
        nexus.with(closure)
    }

    void tasks(@DelegatesTo(DefaultTaskConfig) Closure closure) {
        tasks.with(closure)
    }

    @Deprecated
    String getReleaseBranch() {
        return git.releaseBranch
    }

    @Deprecated
    void setReleaseBranch(String releaseBranch) {
        git.releaseBranch = releaseBranch
    }

    @Deprecated
    boolean getReleaseOnDemand() {
        return trigger.releaseOnDemand
    }

    @Deprecated
    void setReleaseOnDemand(boolean releaseOnDemand) {
        trigger.releaseOnDemand = releaseOnDemand
    }

    @Deprecated
    String getOnDemandReleaseTriggerCommand() {
        return trigger.onDemandReleaseTriggerCommand
    }

    @Deprecated
    void setOnDemandReleaseTriggerCommand(String onDemandReleaseTriggerCommand) {
        trigger.onDemandReleaseTriggerCommand = onDemandReleaseTriggerCommand
    }

    @Deprecated
    boolean getAutoPromote() {
        return nexus.autoPromote
    }

    @Deprecated
    void setAutoPromote(boolean autoPromote) {
        nexus.autoPromote = autoPromote
    }

    //TODO: Work on names...
}
