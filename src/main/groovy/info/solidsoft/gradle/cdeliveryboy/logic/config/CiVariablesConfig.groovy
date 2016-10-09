package info.solidsoft.gradle.cdeliveryboy.logic.config

interface CiVariablesConfig {

    String getIsPrName()
    String getBranchNameName()
    String getCommitMessageName()
    String getRepoSlugName()    //https://en.wikipedia.org/wiki/Semantic_URL#Slug
}