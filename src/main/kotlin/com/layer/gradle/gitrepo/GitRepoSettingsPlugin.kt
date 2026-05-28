package com.layer.gradle.gitrepo

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings
import org.gradle.api.plugins.ExtensionAware

class GitRepoSettingsPlugin : Plugin<Settings> {

    override fun apply(settings: Settings) {
        val repos = settings.dependencyResolutionManagement.repositories
        val ext = GitRepoExtension(SettingsGitRepoContext(settings), repos)
        (repos as ExtensionAware).extensions.add("gitRepo", ext)

        GroovyDslSupport.injectRepositoryMethods(repos, ext)
    }
}
