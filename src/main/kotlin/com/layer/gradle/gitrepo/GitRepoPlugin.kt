package com.layer.gradle.gitrepo

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

class GitRepoPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        if (project.rootProject.tasks.findByName("cleanLocalRepoCache") == null) {
            project.rootProject.tasks.register("cleanLocalRepoCache") { t ->
                t.doFirst { localReposCache.clear() }
            }.also { provider ->
                project.rootProject.pluginManager.withPlugin("base") {
                    project.rootProject.tasks.named("clean").configure { it.dependsOn(provider) }
                }
            }
        }

        val gitRepoExt = GitRepoExtension(ProjectGitRepoContext(project), project.repositories)
        (project.repositories as ExtensionAware).extensions.add("gitRepo", gitRepoExt)

        GroovyDslSupport.injectRepositoryMethods(project.repositories, gitRepoExt)
    }

    companion object {
        const val DEFAULT_BRANCH = "master"
        const val DEFAULT_TYPE = "releases"
        internal val localReposCache: MutableSet<LocalRepo> = mutableSetOf()
    }
}
