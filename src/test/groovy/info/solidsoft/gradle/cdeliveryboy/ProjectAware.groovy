package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.PackageScope
import org.gradle.api.Project

@PackageScope
interface ProjectAware {

    Project getProject()
}