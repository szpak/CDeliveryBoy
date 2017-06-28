package info.solidsoft.gradle.cdeliveryboy.infra

import com.github.zafarkhaja.semver.Version
import groovy.transform.CompileStatic
import org.gradle.api.Project
import pl.allegro.tech.build.axion.release.domain.VersionConfig
import pl.allegro.tech.build.axion.release.domain.VersionIncrementerContext

@CompileStatic
class AxionReleaseVersionSetter implements ReleaseVersionSetter {

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

    private AxionReleaseVersionSetter(VersionConfig axionConfig) {
        this.axionConfig = axionConfig
    }

    /**
     * Sets release version using Axion incrementer.
     *
     * Expects an one of supported incrementers or a Semantic Versioning compilant version number.
     *
     * @param forcedVersion forced version to use
     */
    @Override
    void setReleaseVersion(String forcedVersion) {
        if (SemVerIncrementer.isSupported(forcedVersion)) {
            axionConfig.versionIncrementer(SemVerIncrementer.valueOf(forcedVersion).toAxionIncrementerName())
            return
        }

        axionConfig.versionIncrementer = { VersionIncrementerContext context ->
            return Version.valueOf(forcedVersion)
        }
    }

    static AxionReleaseVersionSetter forProject(Project project) {
        VersionConfig axionConfig = project.extensions.getByType(VersionConfig)
        return new AxionReleaseVersionSetter(axionConfig)
    }
}
