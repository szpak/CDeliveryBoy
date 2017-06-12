package info.solidsoft.gradle.cdeliveryboy.infra

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import org.gradle.api.Project
import pl.allegro.tech.build.axion.release.domain.VersionConfig

import static info.solidsoft.gradle.cdeliveryboy.CDeliveryBoyPlugin.EXTENSION_NAME

@CompileStatic
class DependantPluginsConfigurer {

    private static final String DBOY_DISABLE_PLUGINS_AUTO_CONFIG_PROPERTY_NAME = "${EXTENSION_NAME}.disablePluginsAutoConfig"

    private static final String AXION_PLUGIN_ID = "pl.allegro.tech.build.axion-release"
    private static final String NEXUS_PLUGIN_ID = 'com.bmuschko.nexus'
    private static final String NEXUS_STAGING_PLUGIN_ID = 'io.codearte.nexus-staging'

    private static final String POWERED_BY_BANNER_MESSAGE = "Powered by CDeliveryBoy."
    private static final Closure<String> DEFAULT_RELEASE_COMMIT_MESSAGE_CREATOR = { String version ->
        "Release version: ${version}\n\n[ci skip]".toString() }

    private final Project project

    DependantPluginsConfigurer(Project project) {
        this.project = project
    }

    void applyAndPreconfigureIfNeeded() {
        if (project.hasProperty(DBOY_DISABLE_PLUGINS_AUTO_CONFIG_PROPERTY_NAME)) {   //TODO: Value could be taken into account
            return
        }
        applyPlugins()
        preconfigurePlugins()
    }

    private void applyPlugins() {
        project.apply([plugin: AXION_PLUGIN_ID])
        project.apply([plugin: NEXUS_PLUGIN_ID])
        project.apply([plugin: NEXUS_STAGING_PLUGIN_ID])
    }

    private void preconfigurePlugins() {
        preconfigureAxion()
    }

    @CompileDynamic //TODO: Provide @DelegatesTo and @ParametersFor PR in Axion
    private void preconfigureAxion() {
        VersionConfig axionConfig = project.extensions.getByType(VersionConfig)
        axionConfig.with {
            tag {
                prefix = 'release'
                versionSeparator = '/'
            }
            versionIncrementer('incrementMinor')
            //TODO: Disable checks and other stuff required by Travis
        }
        schedulePreReleaseCommitAddition(axionConfig)
        //Note: 'project.version = project.scmVersion.version' cannot be used due to version caching in Axion
    }

    private void schedulePreReleaseCommitAddition(VersionConfig axionConfig) {
        project.afterEvaluate {
            CDeliveryBoyPluginConfig pluginConfig = project.extensions.getByType(CDeliveryBoyPluginConfig)
            if (pluginConfig.git.createReleaseCommit) {
                addPreReleaseCommitHook(pluginConfig, axionConfig)
            }
        }
    }

    @CompileDynamic
    private void addPreReleaseCommitHook(CDeliveryBoyPluginConfig pluginConfig, VersionConfig axionConfig) {
        Closure<String> releaseCommitMessage = pluginConfig.git.overriddenReleaseCommitMessageCreator ?: DEFAULT_RELEASE_COMMIT_MESSAGE_CREATOR
        String bannerMessage = pluginConfig.git.addPoweredByMessage ? "\n\n" + POWERED_BY_BANNER_MESSAGE : ""

        axionConfig.hooks {
            pre 'commit', { String version, position -> "${releaseCommitMessage(version)}${bannerMessage}" }
        }
    }
}
