package info.solidsoft.gradle.cdeliveryboy.logic.config

import groovy.transform.CompileStatic

@CompileStatic
class CDeliveryBoyPluginConfig {

    String ciType = 'travis'
    String releaseBranch = 'master'
    boolean autoPromote = false   //just to check artifacts manually for the first time
    boolean releaseOnDemand = true
    String onDemandReleaseTriggerCommand = "[#DO_RELEASE]"
    boolean dryRun = false

    //For dryRun/test purpose
    boolean dryRunForceNonSnapshotVersion = false

    DefaultTaskConfig tasks = new DefaultTaskConfig()

    void tasks(@DelegatesTo(DefaultTaskConfig) Closure closure) {
        tasks.with closure
    }

    //TODO: Work on names...
}
