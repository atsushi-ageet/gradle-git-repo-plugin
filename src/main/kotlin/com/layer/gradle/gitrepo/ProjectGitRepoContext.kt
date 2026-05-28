package com.layer.gradle.gitrepo

import org.gradle.api.Project
import java.io.File

internal class ProjectGitRepoContext(private val project: Project) : GitRepoContext {
    override val isOffline: Boolean
        get() = project.gradle.startParameter.isOffline

    override fun repositoryDir(name: String): File {
        return if (project.hasProperty("gitRepoHome")) {
            project.file("${project.property("gitRepoHome")}/$name")
        } else {
            project.file("${System.getProperty("user.home")}/.gitRepos/$name")
        }
    }
}
