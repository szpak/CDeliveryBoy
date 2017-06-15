package info.solidsoft.gradle.cdeliveryboy.infra

import com.github.zafarkhaja.semver.Version
import groovy.transform.CompileStatic
import org.gradle.api.Project
import pl.allegro.tech.build.axion.release.domain.VersionConfig
import pl.allegro.tech.build.axion.release.domain.VersionIncrementerContext

@CompileStatic
class AxionReleaseVersionSetter implements ReleaseVersionSetter {

    private final VersionConfig axionConfig

    private AxionReleaseVersionSetter(VersionConfig axionConfig) {
        this.axionConfig = axionConfig
    }

    @Override
    void setReleaseVersion(String forcedVersion) {
        axionConfig.versionIncrementer = { VersionIncrementerContext context ->
            return Version.valueOf(forcedVersion)
        }
    }

    static AxionReleaseVersionSetter forProject(Project project) {
        VersionConfig axionConfig = project.extensions.getByType(VersionConfig)
        return new AxionReleaseVersionSetter(axionConfig)
    }
}
