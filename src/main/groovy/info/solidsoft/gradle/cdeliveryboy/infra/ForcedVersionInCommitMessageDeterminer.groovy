package info.solidsoft.gradle.cdeliveryboy.infra

import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.logic.ForcedVersion

@CompileStatic
class ForcedVersionInCommitMessageDeterminer {

    ForcedVersion determineForcedVersionInCommitMessage(String commitMessage) {
        //TODO: Not implemented yet
        return ForcedVersion.noVersionForced()
    }
}
