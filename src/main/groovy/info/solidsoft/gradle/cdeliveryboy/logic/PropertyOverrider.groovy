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

    //TODO: Write integration tests to verify boolean logic (e.g. -PdryRun considered as true)
    private boolean getBooleanValueFromPropertyOrFallback(String propertyName, boolean fallbackValue) {
        return g(propertyName, fallbackValue) { it == "" || it.toBoolean() }
    }

    private String getStringValueFromPropertyOrFallback(String propertyName, String fallbackValue) {
        return g(propertyName, fallbackValue) { it }
    }

    private <T> T g(String propertyName, T fallbackValue, StringToValue<T> conversion) {
        String propertyValue = projectPropertyReader.findByName(propertyName)
        return propertyValue != null ? conversion.apply(propertyValue) : fallbackValue
    }

    //To be replaced with Function once migrated to Java 8
    private static interface StringToValue<V> {
        V apply(String propertyValue)
    }
}
