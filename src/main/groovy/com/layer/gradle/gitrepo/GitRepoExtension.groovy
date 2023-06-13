package com.layer.gradle.gitrepo

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.ArtifactRepository

class GitRepoExtension {
    private final RepositoryHandler repositories

    GitRepoExtension(final Project project) {
        this.repositories = project.repositories
    }

    ArtifactRepository github(String org, String repo, String branch = GitRepoPlugin.DEFAULT_BRANCH, String type = GitRepoPlugin.DEFAULT_TYPE) {
        repositories.github(org, repo, branch, type)
    }

    ArtifactRepository bitbucket(String org, String repo, String branch = GitRepoPlugin.DEFAULT_BRANCH, String type = GitRepoPlugin.DEFAULT_TYPE) {
        repositories.bitbucket(org, repo, branch, type)
    }

    ArtifactRepository git(String gitUrl, String name, String branch = GitRepoPlugin.DEFAULT_BRANCH, String type = GitRepoPlugin.DEFAULT_TYPE) {
        repositories.git(gitUrl, name, branch, type)
    }
}
