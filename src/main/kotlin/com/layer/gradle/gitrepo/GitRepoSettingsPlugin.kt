package com.layer.gradle.gitrepo

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.ExtensionAware

class GitRepoSettingsPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        settings.gradle.projectsLoaded { gradle ->
            val rootProject = gradle.rootProject
            if (rootProject.tasks.findByName("cleanLocalRepoCache") == null) {
                val cleanCache = rootProject.tasks.register("cleanLocalRepoCache") { t ->
                    t.doFirst { GitRepoPlugin.localReposCache.clear() }
                }
                rootProject.afterEvaluate {
                    rootProject.tasks.maybeCreate("clean").dependsOn(cleanCache)
                }
            }
        }

        val repos = settings.dependencyResolutionManagement.repositories
        val ext = GitRepoExtension(SettingsGitRepoContext(settings), repos)
        (repos as ExtensionAware).extensions.add("gitRepo", ext)

        GroovyDslSupport.injectRepositoryMethods(repos, ext)
    }
}
