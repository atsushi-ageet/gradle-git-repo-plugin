package com.layer.gradle.gitrepo

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.RefSpec
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.UnknownTaskException
import org.gradle.api.plugins.ExtensionAware

class GitRepoPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        var cleanLocalRepoCache = project.rootProject.tasks.findByName("cleanLocalRepoCache")
        if (cleanLocalRepoCache == null) {
            cleanLocalRepoCache = project.rootProject.tasks.create("cleanLocalRepoCache")
                .also { it.doFirst { localReposCache.clear() } }
            project.rootProject.afterEvaluate {
                project.rootProject.tasks.maybeCreate("clean").dependsOn(cleanLocalRepoCache)
            }
        }

        project.extensions.create("gitPublishConfig", GitPublishConfig::class.java)
        val gitRepoExt = GitRepoExtension(ProjectGitRepoContext(project), project.repositories)
        (project.repositories as ExtensionAware).extensions.add("gitRepo", gitRepoExt)

        GroovyDslSupport.injectRepositoryMethods(project.repositories, gitRepoExt)

        project.afterEvaluate {
            if (project.hasPublishTask) {
                val config = project.extensions.getByType(GitPublishConfig::class.java)

                val cloneRepo = project.tasks.create("cloneRepo")
                cloneRepo.doFirst {
                    gitRepoExt.ensureLocalRepo(
                        gitRepoExt.repositoryDir(config.org),
                        config.repo,
                        config.gitCloneUrl,
                        config.branch
                    )
                }
                val publishTask = project.publishTask
                publishTask.dependsOn(cloneRepo)

                val publishAndPush = project.tasks.create(config.publishAndPushTask)
                publishAndPush.doFirst {
                    val gitDir = gitRepoExt.repositoryDir("${config.org}/${config.repo}")
                    Git.open(gitDir).use { git ->
                        git.add().addFilepattern(".").call()
                        git.commit().setMessage("published artifacts for ${project.group} ${project.version}").call()
                        git.push()
                            .setRefSpecs(RefSpec("HEAD:refs/heads/${config.branch}"))
                            .call()
                    }
                }
                publishAndPush.dependsOn(publishTask)
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

    companion object {
        const val DEFAULT_BRANCH = "master"
        const val DEFAULT_TYPE = "releases"
        internal val localReposCache: MutableSet<LocalRepo> = mutableSetOf()
    }
}
