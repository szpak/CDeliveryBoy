package info.solidsoft.gradle.cdeliveryboy.infra

import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.logic.ForcedVersion

@CompileStatic
class ReleaseVersionDeterminer {

    private final ReleaseVersionOverrider releaseVersionSetter

    ReleaseVersionDeterminer(ReleaseVersionOverrider releaseVersionSetter) {
        this.releaseVersionSetter = releaseVersionSetter
    }

    void determineAndForceReleaseVersionIfRequested(ForcedVersion forcedVersion) {
        if (!forcedVersion.isForced) {
            return
        }
        releaseVersionSetter.overrideReleaseVersion(forcedVersion.forcedValue)
    }
}
