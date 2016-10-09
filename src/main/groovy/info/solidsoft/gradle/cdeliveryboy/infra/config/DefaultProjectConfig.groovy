package info.solidsoft.gradle.cdeliveryboy.infra.config

import info.solidsoft.gradle.cdeliveryboy.logic.config.ProjectConfig
import org.gradle.api.Project

class DefaultProjectConfig implements ProjectConfig {

    private final Project project

    DefaultProjectConfig(Project project) {
        this.project = project
    }

    @Override
    boolean isGlobalDryRun() {
        return project.gradle.startParameter.dryRun
    }

    @Override
    String getVersion() {
        return project.version.toString()
    }
}
