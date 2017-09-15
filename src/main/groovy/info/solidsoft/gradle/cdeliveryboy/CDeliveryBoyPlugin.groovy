package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.CompileStatic
import info.solidsoft.gradle.cdeliveryboy.infra.DependantPluginsConfigurer
import info.solidsoft.gradle.cdeliveryboy.infra.ioc.IocContext
import info.solidsoft.gradle.cdeliveryboy.infra.ioc.ManualIocContext
import info.solidsoft.gradle.cdeliveryboy.infra.task.TaskCreator
import info.solidsoft.gradle.cdeliveryboy.logic.config.CDeliveryBoyPluginConfig
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

@CompileStatic
@SuppressWarnings("GrMethodMayBeStatic")
class CDeliveryBoyPlugin implements Plugin<Project>, CDeliveryBoyPluginConstants {

    private static Logger log = Logging.getLogger(CDeliveryBoyPlugin)

    private IocContext iocContext   //as a field to make it modifiable in integration tests through enhanceOrReplaceContextHackForIntegrationTesting

    @Override
    void apply(Project project) {
        //TODO: Casting is required due to: https://issues.apache.org/jira/browse/GROOVY-7907 - fixed in Groovy 2.4.8
        CDeliveryBoyPluginConfig pluginConfig = (CDeliveryBoyPluginConfig) project.extensions.create(EXTENSION_NAME, CDeliveryBoyPluginConfig) //TODO: one or more extensions?

        TaskCreator taskCreator = new TaskCreator(project)
        CDeliveryBoyCiPrepareTask prepareTask = taskCreator.task(PREPARE_FOR_CI_BUILD_TASK_NAME, CDeliveryBoyCiPrepareTask,
                "Prepares for CI build with optional release")
        CDeliveryBoyCiBuildTask buildTask = taskCreator.task(CI_BUILD_TASK_NAME, CDeliveryBoyCiBuildTask,
                "Performs CI build with optional release")
        PushRelease2Task pushRelease2Task = taskCreator.task(PUSH_RELEASE2_TASK_NAME, PushRelease2Task,
                "Performs second stage of release - pushes tag to remote (workaround on Axion limitation with refspec)")

        new DependantPluginsConfigurer(project).applyAndPreconfigureIfNeeded()  //Not in context as it's to early

        iocContext = new ManualIocContext(project, pluginConfig)

        project.afterEvaluate {

            iocContext.initialize()
            iocContext.with {
                propertyOverrider.applyCommandLineProperties(pluginConfig)
                prepareForCiBuildTaskOrchestrator.orchestrateDependantTasks(prepareTask)
                ciBuildTaskOrchestrator.orchestrateDependantTasks(buildTask)

                taskConfigurer.configurePrepareCiBuildTask(prepareTask)
                taskConfigurer.configureCiBuildTask(buildTask)
                taskConfigurer.configurePushRelease2Task(pushRelease2Task)
            }
        }
    }

    //Switch to Function/UnaryOperator once migrated to Java 8
    private enhanceOrReplaceContextHackForIntegrationTesting(@DelegatesTo(IocContext) Closure<IocContext> f) {
        iocContext = f.call(iocContext)
    }
}
