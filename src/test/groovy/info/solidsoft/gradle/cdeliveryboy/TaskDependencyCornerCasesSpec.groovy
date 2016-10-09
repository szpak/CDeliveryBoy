package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.NotYetImplemented
import spock.lang.Specification

class TaskDependencyCornerCasesSpec extends Specification {

    @NotYetImplemented
    def "should not fail on missing release task if not related task is executed"() {
    }

    @NotYetImplemented
    def "should not fail on missing related tasks if CI task is not called"() { //TODO: Shouldn't?
    }
}
