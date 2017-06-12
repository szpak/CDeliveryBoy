package info.solidsoft.gradle.cdeliveryboy.logic.config

import groovy.transform.CompileStatic

@CompileStatic
class TravisVariablesConfig extends AbstractCiVariablesConfig {

    final String isPrName = 'TRAVIS_PULL_REQUEST'
    final String branchNameName = 'TRAVIS_BRANCH'
    final String commitMessageName = 'TRAVIS_COMMIT_MSG'    //TODO: Not available out-of-box on Travis - https://github.com/travis-ci/travis-ci/issues/965
    final String repoSlugName = "TRAVIS_REPO_SLUG"  //TODO: Not very portable across CI servers
}
