package org.gradle.kotlin.dsl

import com.layer.gradle.gitrepo.GitRepoExtension
import com.layer.gradle.gitrepo.GitRepoPlugin

import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.ArtifactRepository
import org.gradle.api.plugins.ExtensionAware

private val RepositoryHandler.gitRepo: GitRepoExtension get() {
    return (this as? ExtensionAware)!!.extensions.getByName("gitRepo") as GitRepoExtension
}

fun RepositoryHandler.github(org: String, repo: String, branch: String = GitRepoPlugin.DEFAULT_BRANCH, type: String = GitRepoPlugin.DEFAULT_TYPE): ArtifactRepository {
    return gitRepo.github(org, repo, branch, type)
}

fun RepositoryHandler.bitbucket(org: String, repo: String, branch: String = GitRepoPlugin.DEFAULT_BRANCH, type: String = GitRepoPlugin.DEFAULT_TYPE): ArtifactRepository {
    return gitRepo.bitbucket(org, repo, branch, type)
}

fun RepositoryHandler.git(gitUrl: String, name: String, branch: String = GitRepoPlugin.DEFAULT_BRANCH, type: String = GitRepoPlugin.DEFAULT_TYPE): ArtifactRepository {
    return gitRepo.git(gitUrl, name, branch, type)
}
