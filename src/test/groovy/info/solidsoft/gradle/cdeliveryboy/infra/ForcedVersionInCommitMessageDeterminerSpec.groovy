package info.solidsoft.gradle.cdeliveryboy.infra

import info.solidsoft.gradle.cdeliveryboy.logic.ForcedVersion
import spock.lang.Specification

class ForcedVersionInCommitMessageDeterminerSpec extends Specification {

    private ForcedVersionInCommitMessageDeterminer forcedVersionDeterminer = new ForcedVersionInCommitMessageDeterminer()

    def "should find simple version in commit message (#commitMessage)"() {
        when:
            ForcedVersion determinedForcedVersion = forcedVersionDeterminer.determineForcedVersionInCommitMessage(commitMessage)
        then:
            determinedForcedVersion == ForcedVersion.forcedVersionWithValue(expectedVersion)
        where:
            commitMessage               || expectedVersion
            "[#0.1.5]"                  || "0.1.5"
            "Trigger release [#0.1.5]"  || "0.1.5"
            "Trigger release\n[#0.1.5]" || "0.1.5"
            "[#1234.121.51]"            || "1234.121.51"
    }

    def "should find pre-release version in commit message (#commitMessage)"() {
        when:
            ForcedVersion determinedForcedVersion = forcedVersionDeterminer.determineForcedVersionInCommitMessage(commitMessage)
        then:
            determinedForcedVersion == ForcedVersion.forcedVersionWithValue(expectedVersion)
        where:
            commitMessage      || expectedVersion
            "[#0.1.5-beta]"    || "0.1.5-beta"
            "[#1.0.0-alpha1]"  || "1.0.0-alpha1"
            "[#1.0.0-alpha-1]" || "1.0.0-alpha-1"
    }

    def "should find incrementer name in commit message (#commitMessage)"() {
        when:
            ForcedVersion determinedForcedVersion = forcedVersionDeterminer.determineForcedVersionInCommitMessage(commitMessage)
        then:
            determinedForcedVersion == ForcedVersion.forcedVersionWithValue(expectedVersion)
        where:
            commitMessage   || expectedVersion
            "[#MAJOR]"      || "MAJOR"
            "[#MINOR]"      || "MINOR"
            "[#PATCH]"      || "PATCH"
            "[#PRERELEASE]" || "PRERELEASE"
    }

    def "should ignore unknown incrementers in commit message"() {
        when:
            ForcedVersion determinedForcedVersion = forcedVersionDeterminer.determineForcedVersionInCommitMessage("[#UNKNOWN]")
        then:
            determinedForcedVersion == ForcedVersion.noVersionForced()
    }

    def "should prefer exact version over incrementer in commit message (#commitMessage)"() {
        when:
            ForcedVersion determinedForcedVersion = forcedVersionDeterminer.determineForcedVersionInCommitMessage(commitMessage)
        then:
            determinedForcedVersion == ForcedVersion.forcedVersionWithValue(expectedVersion)
        where:
            commitMessage      || expectedVersion
            "[#MAJOR][#1.2.6]" || "1.2.6"
            "[#0.0.1][#MINOR]" || "0.0.1"
    }
}
