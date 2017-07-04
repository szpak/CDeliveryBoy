package info.solidsoft.gradle.cdeliveryboy.infra

import com.github.zafarkhaja.semver.Version
import groovy.transform.CompileStatic
import org.gradle.api.Project
import pl.allegro.tech.build.axion.release.domain.VersionConfig
import pl.allegro.tech.build.axion.release.domain.VersionIncrementerContext

@CompileStatic
class AxionReleaseVersionOverrider implements ReleaseVersionOverrider {

    @CompileStatic
    private enum SemVerIncrementer {

        PATCH, MINOR, MAJOR, PRERELEASE

        String toAxionIncrementerName() {
            return "increment${name().toLowerCase().capitalize()}"
        }

        static boolean isSupported(String incrementer) {
            try {
                valueOf(incrementer)
                return true
            } catch (IllegalArgumentException ignored) {
                return false
            }
        }
    }

    private final VersionConfig axionConfig

    private AxionReleaseVersionOverrider(VersionConfig axionConfig) {
        this.axionConfig = axionConfig
    }

    @Override
    void overrideReleaseVersion(String overriddenVersion) {
        if (SemVerIncrementer.isSupported(overriddenVersion)) {
            axionConfig.versionIncrementer(SemVerIncrementer.valueOf(overriddenVersion).toAxionIncrementerName())
        } else {
            axionConfig.versionIncrementer = { VersionIncrementerContext context ->
                return Version.valueOf(overriddenVersion)
            }
        }
    }

    static AxionReleaseVersionOverrider forProject(Project project) {
        VersionConfig axionConfig = project.extensions.getByType(VersionConfig)
        return new AxionReleaseVersionOverrider(axionConfig)
    }
}
