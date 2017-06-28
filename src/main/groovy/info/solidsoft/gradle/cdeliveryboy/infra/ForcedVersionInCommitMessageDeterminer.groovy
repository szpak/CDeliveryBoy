package info.solidsoft.gradle.cdeliveryboy.infra

import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.logic.ForcedVersion

import java.util.regex.Matcher
import java.util.regex.Pattern

import static info.solidsoft.gradle.cdeliveryboy.logic.ForcedVersion.forcedVersionWithValue
import static info.solidsoft.gradle.cdeliveryboy.logic.ForcedVersion.noVersionForced

@CompileStatic
class ForcedVersionInCommitMessageDeterminer {

    private static final Pattern SIMPLE_SEM_VER_PATTERN = Pattern.compile("\\[#(\\d+\\.\\d+\\.\\d+)\\]")
    //https://github.com/mojombo/semver/issues/232
    private static final Pattern SEM_VER_PATTERN = Pattern.compile("\\[#((0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)" +
            "(-(0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(\\.(0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*)?(\\+[0-9a-zA-Z-]+(\\.[0-9a-zA-Z-]+)*)?)\\]")

    ForcedVersion determineForcedVersionInCommitMessage(String commitMessage) {
        Matcher matcher = SEM_VER_PATTERN.matcher(commitMessage)
        if (matcher.find()) {
            return forcedVersionWithValue(matcher.group(1))
        } else {
            return noVersionForced()
        }
    }
}
