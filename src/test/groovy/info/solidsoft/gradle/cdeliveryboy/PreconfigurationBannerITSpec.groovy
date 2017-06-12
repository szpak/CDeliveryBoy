package info.solidsoft.gradle.cdeliveryboy

import org.gradle.testfixtures.ProjectBuilder
import pl.allegro.tech.build.axion.release.domain.VersionConfig
import pl.allegro.tech.build.axion.release.domain.hooks.CommitHookAction
import pl.allegro.tech.build.axion.release.domain.hooks.HookContext
import pl.allegro.tech.build.axion.release.domain.scm.ScmPosition

//TODO: Quite low level Axion internals testing. Could be done easier?
class PreconfigurationBannerITSpec extends BasicProjectBuilderITSpec {

    private VersionConfig scmVersion
    private HookContext hookContext = Mock()
    private List anyList = _ as List

    def setup() {
        //Override project from BasicProjectBuilderSpec as extra properties (such as "cDeliveryBoy.disablePluginsAutoConfig") cannot be removed
        project = ProjectBuilder.builder().withProjectDir(tmpProjectDir.root).build()
        project.apply(plugin: CDeliveryBoyPlugin)

        scmVersion = getAxionConfiguration()

        hookContext.currentVersion >> "7.1" //deprecated, but still used as of 1.6.0
        hookContext.releaseVersion >> "7.1"
        hookContext.position >> Stub(ScmPosition)
    }

    def "should add commit hook with default message in default configuration"() {
        when:
            triggerEvaluate()
        then:
            scmVersion.hooks.preReleaseHooks.find { it.class == CommitHookAction }
    }

    def "should not add commit hook if disabled in configuration"() {
        given:
            getDeliveryBoyConfig().git.createReleaseCommit = false
        when:
            triggerEvaluate()
        then:
            !scmVersion.hooks.preReleaseHooks.find { it.class == CommitHookAction }
    }

    def "should use default release commit message in default configuration"() {
        given:
            CommitHookAction commitHook = triggerEvaluateAndGetCommitHookAction()
        when:
            commitHook.act(hookContext)
        then:
            1 * hookContext.commit(anyList, { it.startsWith("Release version: 7.1") } as String)
    }

    def "should add powered by banner in default configuration"() {
        given:
            CommitHookAction commitHook = triggerEvaluateAndGetCommitHookAction()
        when:
            commitHook.act(hookContext)
        then:
            1 * hookContext.commit(anyList, { it.contains("CDeliveryBoy") } as String)
    }

    def "should not add powered by banner in explicitly disabled in configuration"() {
        given:
            getDeliveryBoyConfig().git.addPoweredByMessage = false
        and:
            CommitHookAction commitHook = triggerEvaluateAndGetCommitHookAction()
        when:
            commitHook.act(hookContext)
        then:
            1 * hookContext.commit(anyList, { !it.contains("CDeliveryBoy") } as String)
    }

    def "should use overridden commit message if provided"() {
        given:
            getDeliveryBoyConfig().git.overriddenReleaseCommitMessageCreator = { version -> "Test release: $version"}
        and:
            CommitHookAction commitHook = triggerEvaluateAndGetCommitHookAction()
        when:
            commitHook.act(hookContext)
        then:
            1 * hookContext.commit(anyList, { it.startsWith("Test release: 7.1") } as String)
    }

    private VersionConfig getAxionConfiguration() {
        project.getExtensions().getByType(VersionConfig)
    }

    private CommitHookAction triggerEvaluateAndGetCommitHookAction() {
        triggerEvaluate()
        CommitHookAction commitHook = (CommitHookAction) scmVersion.hooks.preReleaseHooks.find { it.class == CommitHookAction }
        assert commitHook
        return commitHook
    }
}
