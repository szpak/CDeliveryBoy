package info.solidsoft.gradle.cdeliveryboy.logic.config

import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.infra.PropertyReader
import info.solidsoft.gradle.cdeliveryboy.logic.exception.MissingRequiredCiVariableException

@CompileStatic
class CiVariablesValidator {

    private final PropertyReader propertyReader
    private final CiVariablesConfig ciVariablesConfig

    CiVariablesValidator(PropertyReader propertyReader, CiVariablesConfig ciVariablesConfig) {
        this.propertyReader = propertyReader
        this.ciVariablesConfig = ciVariablesConfig
    }

    void checkExistence() {
        List<String> missingVariableNames = ciVariablesConfig.allVariableNames.findAll { propertyReader.findByName(it) == null }

        if (missingVariableNames) {
            throw new MissingRequiredCiVariableException(missingVariableNames)
        }
    }
}
