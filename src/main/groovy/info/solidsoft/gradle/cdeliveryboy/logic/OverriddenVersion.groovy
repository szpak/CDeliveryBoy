package info.solidsoft.gradle.cdeliveryboy.logic

import groovy.transform.Canonical
import groovy.transform.CompileStatic

@CompileStatic
@Canonical
class OverriddenVersion {

    private static final OverriddenVersion noOverriddenVersion = new OverriddenVersion(false, null)

    final boolean isOverridden
    final String overriddenValue

    private OverriddenVersion(boolean isOverridden, String overriddenValue) {
        this.isOverridden = isOverridden
        this.overriddenValue = overriddenValue
    }
    
    static OverriddenVersion noVersionOverridden() {
        return noOverriddenVersion;
    }

    static OverriddenVersion overriddenVersionWithValue(String overriddenValue) {
        return new OverriddenVersion(true, overriddenValue)
    }

    @Override
    String toString() {
        if (isOverridden) {
            return "overridden: $overriddenValue"
        } else {
            return "no-overridden-version"
        }
    }
}
