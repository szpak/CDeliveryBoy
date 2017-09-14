package info.solidsoft.gradle.cdeliveryboy.logic

import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import info.solidsoft.gradle.cdeliveryboy.infra.ProjectPropertyReader

import static info.solidsoft.gradle.cdeliveryboy.CDeliveryBoyPluginConstants.EXTENSION_NAME

@CompileStatic
class PropertyOverrider {

    private final ProjectPropertyReader projectPropertyReader

    PropertyOverrider(ProjectPropertyReader projectPropertyReader) {
        this.projectPropertyReader = projectPropertyReader
    }

    void applyCommandLineProperties(CDeliveryBoyPluginConfig pluginConfig) {
        //TODO: Go through all fields (simple types) in pluginConfig and try to determine property value to avoid error prone constructions + unit tests?
        pluginConfig.dryRun = getBooleanValueFromPropertyOrFallback("${EXTENSION_NAME}.dryRun", pluginConfig.dryRun)
        pluginConfig.dryRunForceNonSnapshotVersion = getBooleanValueFromPropertyOrFallback("${EXTENSION_NAME}.dryRunForceNonSnapshotVersion", pluginConfig.dryRunForceNonSnapshotVersion)
    }

    //TODO: Write tests to verify boolean logic (e.g. -PdryRun considered as true)
    private boolean getBooleanValueFromPropertyOrFallback(String propertyName, boolean fallbackValue) {
        String projectProperty = projectPropertyReader.findByName(propertyName)
        if (projectProperty != null) {
            return projectProperty == "" || projectProperty.toBoolean()
        } else {
            return fallbackValue
        }
    }

    //TODO: Remove duplication - with Closure constants doing a logic to determine value?
    private String getStringValueFromPropertyOrFallback(String propertyName, boolean fallbackValue) {
        String projectProperty = projectPropertyReader.findByName(propertyName)
        if (projectProperty != null) {
            return projectProperty
        } else {
            return fallbackValue
        }
    }
}
