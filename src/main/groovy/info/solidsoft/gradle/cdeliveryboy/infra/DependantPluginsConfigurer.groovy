package info.solidsoft.gradle.cdeliveryboy.infra

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import org.gradle.api.Project
import pl.allegro.tech.build.axion.release.domain.VersionConfig
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition

import static info.solidsoft.gradle.cdeliveryboy.CDeliveryBoyPlugin.EXTENSION_NAME

@CompileStatic
class DependantPluginsConfigurer {

    private static final String DBOY_DISABLE_PLUGINS_AUTO_CONFIG_PROPERTY_NAME = "${EXTENSION_NAME}.disablePluginsAutoConfig"

    private static final String AXION_PLUGIN_ID = "pl.allegro.tech.build.axion-release"
    private static final String NEXUS_PLUGIN_ID = 'com.bmuschko.nexus'
    private static final String NEXUS_STAGING_PLUGIN_ID = 'io.codearte.nexus-staging'

    private static final String POWERED_BY_BANNER_MESSAGE = "Powered by CDeliveryBoy."

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

    @CompileDynamic
    private void preconfigureAxion() {
        VersionConfig axionConfig = project.extensions.getByType(VersionConfig)
        axionConfig.with {
            tag {
                prefix = 'release'
                versionSeparator = '/'
            }
            createReleaseCommit = true
            releaseCommitMessage { version, position -> "Release version: ${version}\n\n[ci skip]" }
            //TODO: Disable checks and other stuff required by Travis
        }
        scheduleBannerAdditionToCommitMessageIfEnabledInConfiguration(axionConfig)
        //Note: 'project.version = project.scmVersion.version' cannot be used due to version caching in Axion
    }

    private void scheduleBannerAdditionToCommitMessageIfEnabledInConfiguration(VersionConfig axionConfig) {
        //it needs to be postponed as user provided configuration is resolved at time when plugins are applied
        project.afterEvaluate {
            CDeliveryBoyPluginConfig pluginConfig = project.extensions.getByType(CDeliveryBoyPluginConfig)
            if (!pluginConfig.git.disablePoweredByMessage) {
                addBannerToCommitMessage(axionConfig)
            }
        }
    }

    private void addBannerToCommitMessage(VersionConfig axionConfig) {
        Closure<String> oldReleaseCommitMessageClosure = axionConfig.releaseCommitMessage
        axionConfig.releaseCommitMessage = { String version, ScmPosition position ->
            return oldReleaseCommitMessageClosure(version, position) + "\n\n" + POWERED_BY_BANNER_MESSAGE
        }
    }
}
