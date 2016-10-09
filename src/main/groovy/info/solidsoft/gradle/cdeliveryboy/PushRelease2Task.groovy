package info.solidsoft.gradle.cdeliveryboy

import groovy.transform.CompileStatic
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

//TODO: Switch to Axion when refspec can be defined
@CompileStatic
class PushRelease2Task extends Exec {

    @Input
    String repoAsSlug

    @Input
    String releaseBranch

    @TaskAction
    @Override
    protected void exec() {
        //TODO: Make CI agnostic if would may stay longer
        commandLine("sh", "-c", """
              git push "https://\$GH_TOKEN@github.com/${getRepoAsSlug()}.git" HEAD:refs/heads/${getReleaseBranch()} --tags 2>&1 | sed "s/\$GH_TOKEN/xxx/g"
            """)
        super.exec()
    }
}
