package info.solidsoft.gradle.cdeliveryboy.logic

import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.infra.OverriddenVersionInCommitMessageFinder
import info.solidsoft.gradle.cdeliveryboy.infra.PropertyReader
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.ProjectConfig

@CompileStatic
class BuildConditionEvaluator {

    private final CiVariablesConfig ciConfig
    private final CDeliveryBoyPluginConfig pluginConfig
    private final PropertyReader environmentVariableReader
    private final ProjectConfig projectConfig
    private final OverriddenVersionInCommitMessageFinder overriddenVersionDeterminer

    BuildConditionEvaluator(CiVariablesConfig ciConfig, CDeliveryBoyPluginConfig pluginConfig, PropertyReader environmentVariableReader,
                            ProjectConfig projectConfig, OverriddenVersionInCommitMessageFinder overriddenVersionDeterminer) {
        this.ciConfig = ciConfig
        this.pluginConfig = pluginConfig
        this.environmentVariableReader = environmentVariableReader
        this.projectConfig = projectConfig
        this.overriddenVersionDeterminer = overriddenVersionDeterminer
    }

    boolean isInReleaseMode() {
        return isInReleaseBranch() && isReleaseTriggered()
    }

    boolean isInReleaseBranch() {   //TODO: make private
        return !isPrBuild() &&
                getActualBranchName() == pluginConfig.git.releaseBranch
    }

    private String getActualBranchName() {
        return environmentVariableReader.findByName(ciConfig.branchNameName)
    }

    private boolean isPrBuild() {
        return environmentVariableReader.findByName(ciConfig.isPrName) != "false"
    }

    private boolean isReleaseTriggered() {
        if (isSkippedByEnvVariable()) {
            return false
        }
        return !pluginConfig.trigger.releaseOnDemand ||
                isReleaseOnDemandTriggered()
    }

    private boolean isSkippedByEnvVariable() {
        return environmentVariableReader.findByName(pluginConfig.trigger.skipReleaseVariableName) == "true"
    }

    private boolean isReleaseOnDemandTriggered() {
        return environmentVariableReader.findByName(ciConfig.commitMessageName)?.contains(pluginConfig.trigger.onDemandReleaseTriggerCommand)
    }

    boolean isSnapshotVersion() {
        return projectConfig.version.endsWith('-SNAPSHOT') && !isInDryRunModeWithForcedNonSnapshotVersion()
    }

    private boolean isInDryRunModeWithForcedNonSnapshotVersion() {
        return (pluginConfig.dryRun || projectConfig.globalDryRun) && pluginConfig.dryRunForceNonSnapshotVersion
    }

    OverriddenVersion overriddenVersion() {
        String commitMessage = environmentVariableReader.findByName(ciConfig.commitMessageName)
        return overriddenVersionDeterminer.findOverriddenVersionInCommitMessageIfProvided(commitMessage)
    }

    String getReleaseConditionsAsString() {
        return """
${f isInReleaseMode()} IN RELEASE MODE
  ${f isInReleaseBranch()} in release branch
    ${renderBranchCondition()}
    ${f !isPrBuild()} not PR build
  ${f isReleaseTriggered()} release triggered
    ${f !isSkippedByEnvVariable()} not skipped by env variable ('${pluginConfig.trigger.skipReleaseVariableName}')
    ${f pluginConfig.trigger.releaseOnDemand} release on demand required
    ${f isReleaseOnDemandTriggered()} release on demand triggered ('${pluginConfig.trigger.onDemandReleaseTriggerCommand}')
  ${f isSnapshotVersion()} is SNAPSHOT
  - current version: '${projectConfig.version}'
  - overridden version: '${overriddenVersion().isOverridden ? overriddenVersion().overriddenValue : "none"}'
"""
    }

    private f(boolean booleanToFormat) {
        return booleanToFormat ? "✔" : "✘"
    }

    private String renderBranchCondition() {
        boolean isBranchMatches = pluginConfig.git.releaseBranch == getActualBranchName()
        String actualPart = !isBranchMatches ? ", actual: '${getActualBranchName()}'" : ''
        return "${f isBranchMatches} configured: '${pluginConfig.git.releaseBranch}'${actualPart}"
    }

    @Deprecated
    String getReleaseConditionsAsStringOld() {
        //TODO: Maybe on lifecycle display only not satisfied conditions (how to do it in a clearly way)?
        return "Branch name: ${environmentVariableReader.findByName(ciConfig.branchNameName)} (configured: ${pluginConfig.git.releaseBranch}), " +
                "is PR: ${environmentVariableReader.findByName(ciConfig.isPrName)}, " +
                "release on demand: ${pluginConfig.trigger.releaseOnDemand}, " +
                "on demand trigger command: '${environmentVariableReader.findByName(ciConfig.commitMessageName)}' " +
                "(configured: '${pluginConfig.trigger.onDemandReleaseTriggerCommand})', " +
                "is SNAPSHOT: '${isSnapshotVersion()}', " +
                "overridden version: '${overriddenVersion()}'"
    }
}
