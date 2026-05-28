package com.layer.gradle.gitrepo

import org.gradle.api.initialization.Settings
import java.io.File

internal class SettingsGitRepoContext(private val settings: Settings) : GitRepoContext {
    override val isOffline: Boolean
        get() = settings.gradle.startParameter.isOffline

    override fun repositoryDir(name: String): File {
        val gitRepoHome = settings.gradle.startParameter.projectProperties["gitRepoHome"]
            ?: System.getProperty("gitRepoHome")
        return if (gitRepoHome != null) {
            File(settings.rootDir, "$gitRepoHome/$name")
        } else {
            File("${System.getProperty("user.home")}/.gitRepos/$name")
        }
    }
}
