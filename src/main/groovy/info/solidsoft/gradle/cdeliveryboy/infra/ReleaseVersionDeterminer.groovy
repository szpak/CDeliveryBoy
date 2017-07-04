package info.solidsoft.gradle.cdeliveryboy.infra

import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.logic.OverriddenVersion

@CompileStatic
class ReleaseVersionDeterminer {

    private final ReleaseVersionOverrider releaseVersionSetter

    ReleaseVersionDeterminer(ReleaseVersionOverrider releaseVersionSetter) {
        this.releaseVersionSetter = releaseVersionSetter
    }

    void determineAndOverrideReleaseVersionIfRequested(OverriddenVersion overriddenVersion) {
        if (!overriddenVersion.isOverridden) {
            return
        }
        releaseVersionSetter.overrideReleaseVersion(overriddenVersion.overriddenValue)
    }
}
