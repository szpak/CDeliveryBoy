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
            //TODO: Disable checks and other stuff required by Travis
            //TODO: minor incrementer - can be overridden in configuration by user
        }
        schedulePreReleaseCommitAddition(axionConfig)
        //Note: 'project.version = project.scmVersion.version' cannot be used due to version caching in Axion
    }

    @CompileDynamic
    def schedulePreReleaseCommitAddition(VersionConfig axionConfig) {
        project.afterEvaluate {
            //TODO: Provide @DelegatesTo and @ParametersFor PR in Axion
            CDeliveryBoyPluginConfig pluginConfig = project.extensions.getByType(CDeliveryBoyPluginConfig)
            String bannerMessage = pluginConfig.git.disablePoweredByMessage ? "" : "\n\n" + POWERED_BY_BANNER_MESSAGE
            axionConfig.hooks {
                //TODO: How to make commit message configurable in configuration? Template engine https://stackoverflow.com/a/37380388 ?
                //      Where "version" could be evaluated?
                pre 'commit', { version, position -> "Release version: ${version}\n\n[ci skip]${bannerMessage}"}
            }

        }

    }
}
