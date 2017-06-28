package info.solidsoft.gradle.cdeliveryboy.logic

import groovy.transform.Canonical
import groovy.transform.CompileStatic

@CompileStatic
@Canonical
class ForcedVersion {

    private static final ForcedVersion noForcedVersion = new ForcedVersion(false, null)

    final boolean isForced
    final String forcedValue

    private ForcedVersion(boolean isForced, String forcedValue) {
        this.isForced = isForced
        this.forcedValue = forcedValue
    }
    
    static ForcedVersion noVersionForced() {
        return noForcedVersion;
    }

    static ForcedVersion forcedVersionWithValue(String forcedValue) {
        return new ForcedVersion(true, forcedValue)
    }

    @Override
    String toString() {
        if (isForced) {
            return "forced: $forcedValue"
        } else {
            return "no-forced-version"
        }
    }
}
