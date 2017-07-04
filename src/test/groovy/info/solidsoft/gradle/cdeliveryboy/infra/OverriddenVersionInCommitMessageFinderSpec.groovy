package info.solidsoft.gradle.cdeliveryboy.infra

import info.solidsoft.gradle.cdeliveryboy.logic.OverriddenVersion
import spock.lang.Specification

class OverriddenVersionInCommitMessageFinderSpec extends Specification {

    private OverriddenVersionInCommitMessageFinder overriddenVersionDeterminer = new OverriddenVersionInCommitMessageFinder()

    def "should find simple version in commit message (#commitMessage)"() {
        when:
            OverriddenVersion overriddenVersion = overriddenVersionDeterminer.findOverriddenVersionInCommitMessageIfProvided(commitMessage)
        then:
            overriddenVersion == OverriddenVersion.overriddenVersionWithValue(expectedVersion)
        where:
            commitMessage               || expectedVersion
            "[#0.1.5]"                  || "0.1.5"
            "Trigger release [#0.1.5]"  || "0.1.5"
            "Trigger release\n[#0.1.5]" || "0.1.5"
            "[#1234.121.51]"            || "1234.121.51"
    }

    def "should find pre-release version in commit message (#commitMessage)"() {
        when:
            OverriddenVersion overriddenVersion = overriddenVersionDeterminer.findOverriddenVersionInCommitMessageIfProvided(commitMessage)
        then:
            overriddenVersion == OverriddenVersion.overriddenVersionWithValue(expectedVersion)
        where:
            commitMessage      || expectedVersion
            "[#0.1.5-beta]"    || "0.1.5-beta"
            "[#1.0.0-alpha1]"  || "1.0.0-alpha1"
            "[#1.0.0-alpha-1]" || "1.0.0-alpha-1"
    }

    def "should find incrementer name in commit message (#commitMessage)"() {
        when:
            OverriddenVersion overriddenVersion = overriddenVersionDeterminer.findOverriddenVersionInCommitMessageIfProvided(commitMessage)
        then:
            overriddenVersion == OverriddenVersion.overriddenVersionWithValue(expectedVersion)
        where:
            commitMessage   || expectedVersion
            "[#MAJOR]"      || "MAJOR"
            "[#MINOR]"      || "MINOR"
            "[#PATCH]"      || "PATCH"
            "[#PRERELEASE]" || "PRERELEASE"
    }

    def "should ignore unknown incrementers in commit message"() {
        when:
            OverriddenVersion overriddenVersion = overriddenVersionDeterminer.findOverriddenVersionInCommitMessageIfProvided("[#UNKNOWN]")
        then:
            overriddenVersion == OverriddenVersion.noVersionOverridden()
    }

    def "should prefer exact version over incrementer in commit message (#commitMessage)"() {
        when:
            OverriddenVersion overriddenVersion = overriddenVersionDeterminer.findOverriddenVersionInCommitMessageIfProvided(commitMessage)
        then:
            overriddenVersion == OverriddenVersion.overriddenVersionWithValue(expectedVersion)
        where:
            commitMessage      || expectedVersion
            "[#MAJOR][#1.2.6]" || "1.2.6"
            "[#0.0.1][#MINOR]" || "0.0.1"
    }
}
