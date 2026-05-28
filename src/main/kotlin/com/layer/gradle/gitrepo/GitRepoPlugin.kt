package com.layer.gradle.gitrepo

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

class GitRepoPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        var cleanLocalRepoCache = project.rootProject.tasks.findByName("cleanLocalRepoCache")
        if (cleanLocalRepoCache == null) {
            cleanLocalRepoCache = project.rootProject.tasks.register("cleanLocalRepoCache")
                .also { it.configure { t -> t.doFirst { localReposCache.clear() } } }.get()
            project.rootProject.afterEvaluate {
                project.rootProject.tasks.maybeCreate("clean").dependsOn(cleanLocalRepoCache)
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
