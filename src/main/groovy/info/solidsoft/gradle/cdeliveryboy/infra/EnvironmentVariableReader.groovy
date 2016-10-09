package info.solidsoft.gradle.cdeliveryboy.infra

import groovy.transform.CompileStatic

@CompileStatic
class EnvironmentVariableReader implements PropertyReader {

    @Override
    String findByName(String propertyName) {
        return System.getenv(propertyName)
    }
}
