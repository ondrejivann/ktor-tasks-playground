import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

open class InstallGitHooksTask : DefaultTask() {
    init {
        group = "git hooks"
        description = "install Git hooks z git-config/hooks into .git/hooks"
    }

    @TaskAction
    fun install() {
        val gitDir = project.rootDir.resolve(".git")
        val sourceDir = project.file("git-config/hooks")
        val targetDir = project.file(".git/hooks")

        if (!gitDir.exists()) {
            logger.lifecycle("No .git directory found; skipping Git hooks installation.")
            return
        }

        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }

        sourceDir.listFiles()?.forEach { hookFile ->
            project.copy {
                from(hookFile)
                into(targetDir)
                fileMode = 0b111101101
            }
            logger.lifecycle("Git hook installed: ${hookFile.name}")
        }
    }
}
