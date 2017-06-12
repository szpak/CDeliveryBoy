package info.solidsoft.gradle.cdeliveryboy.logic.config

import groovy.transform.CompileStatic
import groovy.transform.PackageScope

@PackageScope
@CompileStatic
abstract class AbstractCiVariablesConfig implements CiVariablesConfig {

    @Override
    List<String> getAllVariableNames() {
        return [getIsPrName(), getBranchNameName(), getCommitMessageName(), getRepoSlugName()]
    }
}
