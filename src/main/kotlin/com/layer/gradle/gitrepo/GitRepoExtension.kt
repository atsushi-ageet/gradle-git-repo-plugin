package com.layer.gradle.gitrepo

import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.ArtifactRepository
import java.io.File

class GitRepoExtension internal constructor(
    private val context: GitRepoContext,
    private val repositoryHandler: RepositoryHandler,
    private val createLocalBranch: Boolean = false,
) {
    fun github(org: String, repo: String, branch: String = GitRepoPlugin.DEFAULT_BRANCH, type: String = GitRepoPlugin.DEFAULT_TYPE, name: String = "github"): ArtifactRepository {
        return addLocalRepo(ensureLocalRepo(context.repositoryDir(org), repo, "git@github.com:$org/$repo.git", branch), type, name)
    }

    fun bitbucket(org: String, repo: String, branch: String = GitRepoPlugin.DEFAULT_BRANCH, type: String = GitRepoPlugin.DEFAULT_TYPE, name: String = "bitbucket"): ArtifactRepository {
        return addLocalRepo(ensureLocalRepo(context.repositoryDir(org), repo, "git@bitbucket.org:$org/$repo.git", branch), type, name)
    }

    fun git(gitUrl: String, name: String, branch: String = GitRepoPlugin.DEFAULT_BRANCH, type: String = GitRepoPlugin.DEFAULT_TYPE, mavenName: String = name): ArtifactRepository {
        return addLocalRepo(ensureLocalRepo(context.repositoryDir(name), name, gitUrl, branch), type, mavenName)
    }

    internal fun repositoryDir(name: String): File = context.repositoryDir(name)

    internal fun ensureLocalRepo(directory: File, name: String, gitUrl: String, branch: String): File {
        val repoDir = File(directory, name)
        if (context.isOffline) return repoDir
        val localRepo = LocalRepo(directory, name, gitUrl, branch)
        if (GitRepoPlugin.localReposCache.contains(localRepo)) return repoDir

        GrgitHelper.ensureLocalRepo(directory, name, gitUrl, branch, createLocalBranch)
        GitRepoPlugin.localReposCache.add(localRepo)
        return repoDir
    }

    private fun addLocalRepo(repoDir: File, type: String, name: String): ArtifactRepository {
        return repositoryHandler.maven(Action { repo ->
            repo.name = name
            repo.url = File(repoDir, type).toURI()
        })
    }
}
