package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.infra.DependantPluginsConfigurer
import info.solidsoft.gradle.cdeliveryboy.infra.ioc.IocContext
import info.solidsoft.gradle.cdeliveryboy.infra.ioc.ManualIocContext
import info.solidsoft.gradle.cdeliveryboy.infra.PropertyReader
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import info.solidsoft.gradle.cdeliveryboy.logic.config.CiVariablesConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

@CompileStatic
@SuppressWarnings("GrMethodMayBeStatic")
class CDeliveryBoyPlugin implements Plugin<Project>, CDeliveryBoyPluginConstants {

    private static Logger log = Logging.getLogger(CDeliveryBoyPlugin)

    private Project project //???

    private IocContext iocContext   //as a field to make it modifiable in integration tests through enhanceOrReplaceContextHackForIntegrationTesting

    @Override
    void apply(Project project) {
        this.project = project

        //TODO: Casting is required due to: https://issues.apache.org/jira/browse/GROOVY-7907 - fixed in Groovy 2.4.8
        CDeliveryBoyPluginConfig pluginConfig = (CDeliveryBoyPluginConfig) project.extensions.create(EXTENSION_NAME, CDeliveryBoyPluginConfig) //TODO: one or more extensions?

        PrepareForCiBuildTaskInstantiator prepareTaskInstantiator = new PrepareForCiBuildTaskInstantiator(project, pluginConfig)
        CDeliveryBoyCiPrepareTask prepareTask = prepareTaskInstantiator.createAndConfigureAndReturn()

        CiBuildTaskInstantiator buildCiTaskInstantiator = new CiBuildTaskInstantiator(project, pluginConfig)
        CDeliveryBoyCiBuildTask buildTask = buildCiTaskInstantiator.createAndConfigureAndReturn()

        PushReleaseTaskInstantiator pushReleaseTaskInstantiator = new PushReleaseTaskInstantiator(project, pluginConfig)
        PushRelease2Task pushRelease2Task = pushReleaseTaskInstantiator.createPushRelease2Task()

        new DependantPluginsConfigurer(project).applyAndPreconfigureIfNeeded()  //Not in context as it's to early

        iocContext = new ManualIocContext(project, pluginConfig)

        project.afterEvaluate {

            iocContext.initialize()
            iocContext.with {
                propertyOverrider.applyCommandLineProperties(pluginConfig)
                prepareForCiBuildTaskOrchestrator.orchestrateDependantTasks(prepareTask)
                ciBuildTaskOrchestrator.orchestrateDependantTasks(buildTask)
                configurePushRelease2Task(pushRelease2Task, pluginConfig, ciVariablesConfig, envVariableReader)
            }
        }
    }

    @CompileDynamic
    private Closure<String> configurePushRelease2Task(PushRelease2Task pushRelease2Task, CDeliveryBoyPluginConfig pluginConfig,
                                                      CiVariablesConfig ciVariablesConfig, PropertyReader propertyReader) {
        pushRelease2Task.conventionMapping.with {
            repoAsSlug = { propertyReader.findByName(ciVariablesConfig.repoSlugName) }
            releaseBranch = { pluginConfig.git.releaseBranch }
        }
    }

    private enhanceOrReplaceContextHackForIntegrationTesting(@DelegatesTo(IocContext) Closure<IocContext> f) {   //Function interface is not available in Java 7
        iocContext = f.call(iocContext)
    }
}
