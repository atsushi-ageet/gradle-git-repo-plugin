package com.layer.gradle.gitrepo

import org.eclipse.jgit.api.Git
import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.ArtifactRepository
import java.io.File

class GitRepoExtension internal constructor(
    private val context: GitRepoContext,
    private val repositoryHandler: RepositoryHandler
) {
    fun github(org: String, repo: String, branch: String = GitRepoPlugin.DEFAULT_BRANCH, type: String = GitRepoPlugin.DEFAULT_TYPE): ArtifactRepository {
        return addLocalRepo(ensureLocalRepo(context.repositoryDir(org), repo, "git@github.com:$org/$repo.git", branch), type)
    }

    fun bitbucket(org: String, repo: String, branch: String = GitRepoPlugin.DEFAULT_BRANCH, type: String = GitRepoPlugin.DEFAULT_TYPE): ArtifactRepository {
        return addLocalRepo(ensureLocalRepo(context.repositoryDir(org), repo, "git@bitbucket.org:$org/$repo.git", branch), type)
    }

    fun git(gitUrl: String, name: String, branch: String = GitRepoPlugin.DEFAULT_BRANCH, type: String = GitRepoPlugin.DEFAULT_TYPE): ArtifactRepository {
        return addLocalRepo(ensureLocalRepo(context.repositoryDir(name), name, gitUrl, branch), type)
    }

    internal fun repositoryDir(name: String): File = context.repositoryDir(name)

    internal fun ensureLocalRepo(directory: File, name: String, gitUrl: String, branch: String): File {
        val repoDir = File(directory, name)
        if (context.isOffline) return repoDir
        val localRepo = LocalRepo(directory, name, gitUrl, branch)
        if (GitRepoPlugin.localReposCache.contains(localRepo)) return repoDir

        if (repoDir.isDirectory) {
            Git.open(repoDir).use { git ->
                git.fetch().call()
                checkoutRemoteCommit(git, branch)
            }
        } else {
            Git.cloneRepository()
                .setDirectory(repoDir)
                .setURI(gitUrl)
                .setNoCheckout(true)
                .call()
                .use { git -> checkoutRemoteCommit(git, branch) }
        }

        GitRepoPlugin.localReposCache.add(localRepo)
        return repoDir
    }

    private fun checkoutRemoteCommit(git: Git, branch: String) {
        val commitId = git.repository.resolve("refs/remotes/origin/$branch")
            ?: error("Remote branch origin/$branch not found")
        git.checkout().setName(commitId.name).call()
    }

    private fun addLocalRepo(repoDir: File, type: String): ArtifactRepository {
        return repositoryHandler.maven(Action { repo ->
            repo.url = File(repoDir, type).toURI()
        })
    }
}
