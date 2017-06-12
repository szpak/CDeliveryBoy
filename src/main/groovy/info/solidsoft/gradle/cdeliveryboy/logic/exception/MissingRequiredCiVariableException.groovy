package info.solidsoft.gradle.cdeliveryboy.logic.exception

import groovy.transform.CompileStatic

@CompileStatic
class MissingRequiredCiVariableException extends RuntimeException {

    final List<String> missingVariableNames

    MissingRequiredCiVariableException(List<String> missingVariableNames) {
        super("Missing CI variables: ${missingVariableNames.size()} - ${missingVariableNames}")
        this.missingVariableNames = missingVariableNames.asImmutable()
    }
}
