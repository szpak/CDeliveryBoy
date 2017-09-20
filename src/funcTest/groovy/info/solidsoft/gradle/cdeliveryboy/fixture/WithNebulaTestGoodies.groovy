package info.solidsoft.gradle.cdeliveryboy.fixture

import groovy.transform.CompileStatic

//Taken from nebula-test project
@CompileStatic
trait WithNebulaTestGoodies {

    void checkForDeprecationsInOutput(String output) {
        def deprecations = output.readLines().findAll {
            it.contains("has been deprecated and is scheduled to be removed in Gradle")
        }
        if (!System.getProperty("ignoreDeprecations") && !deprecations.isEmpty()) {
            throw new IllegalArgumentException("Deprecation warnings were found (Set the ignoreDeprecations system property during the test to ignore):\n" + deprecations.collect {
                " - $it"
            }.join("\n"))
        }
    }
}
