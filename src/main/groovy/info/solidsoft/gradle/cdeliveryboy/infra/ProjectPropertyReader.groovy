package info.solidsoft.gradle.cdeliveryboy.infra

import groovy.transform.CompileStatic
import org.gradle.api.Project

@CompileStatic
class ProjectPropertyReader implements PropertyReader {

    private final Project project

    ProjectPropertyReader(Project project) {
        this.project = project
    }

    @Override
    String findByName(String propertyName) {
        return project.findProperty(propertyName)
    }
}
