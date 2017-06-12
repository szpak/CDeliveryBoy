package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.PackageScope
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

@PackageScope
abstract class BasicProjectBuilderSpec extends Specification implements TaskFixtureTrait, ProjectAware{

    @Rule
    public TemporaryFolder tmpProjectDir = new TemporaryFolder()

    Project project

    //TODO: There is a regression in 2.14.1 with API jar regeneration for every test - https://discuss.gradle.org/t/performance-regression-in-projectbuilder-in-2-14-and-3-0/18956
    //https://github.com/gradle/gradle/commit/3216f07b3acb4cbbb8241d8a1d50b8db9940f37e
    def setup() {
        project = ProjectBuilder.builder().withProjectDir(tmpProjectDir.root).build()

        project.extensions.extraProperties.set("cDeliveryBoy.disablePluginsAutoConfig", "true") //speed up testing, extra plugins are not needed here
    }
}