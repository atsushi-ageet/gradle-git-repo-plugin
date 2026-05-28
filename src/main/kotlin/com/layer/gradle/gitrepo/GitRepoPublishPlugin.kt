package com.layer.gradle.gitrepo

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.RefSpec
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import org.gradle.api.plugins.ExtensionAware

class GitRepoPublishPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply(GitRepoPlugin::class.java)

        project.extensions.create("gitPublishConfig", GitPublishConfig::class.java)

        project.afterEvaluate {
            if (project.hasPublishTask) {
                val config = project.extensions.getByType(GitPublishConfig::class.java)
                val gitRepoExt = (project.repositories as ExtensionAware).extensions.getByName("gitRepo") as GitRepoExtension

                val cloneRepo = project.tasks.register("cloneRepo") { t ->
                    t.doFirst {
                        gitRepoExt.ensureLocalRepo(
                            gitRepoExt.repositoryDir(config.org),
                            config.repo,
                            config.gitCloneUrl,
                            config.branch
                        )
                    }
                }.get()
                project.publishTask.dependsOn(cloneRepo)

                val publishAndPush = project.tasks.register(config.publishAndPushTask) { t ->
                    t.doFirst {
                        val gitDir = gitRepoExt.repositoryDir("${config.org}/${config.repo}")
                        Git.open(gitDir).use { git ->
                            git.add().addFilepattern(".").call()
                            git.commit().setMessage("published artifacts for ${project.group} ${project.version}").call()
                            git.push()
                                .setRefSpecs(RefSpec("HEAD:refs/heads/${config.branch}"))
                                .call()
                        }
                    }
                }.get()
                publishAndPush.dependsOn(project.publishTask)
            }
        }
    }

    private val Project.hasPublishTask: Boolean get() = try {
        publishTask
        true
    } catch (e: UnknownTaskException) {
        false
    }

    private val Project.publishTask: Task get() = tasks.getByName(
        extensions.getByType(GitPublishConfig::class.java).publishTask
    )

    private val GitPublishConfig.gitCloneUrl: String get() = gitUrl.ifEmpty {
        "git@${provider}:${org}/${repo}.git"
    }
}
