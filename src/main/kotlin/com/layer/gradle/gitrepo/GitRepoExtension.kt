package com.layer.gradle.gitrepo

import org.eclipse.jgit.api.Git
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.ArtifactRepository
import java.io.File

class GitRepoExtension(private val project: Project) {

    fun github(org: String, repo: String, branch: String = GitRepoPlugin.DEFAULT_BRANCH, type: String = GitRepoPlugin.DEFAULT_TYPE): ArtifactRepository {
        return addLocalRepo(ensureLocalRepo(repositoryDir(org), repo, "git@github.com:$org/$repo.git", branch), type)
    }

    fun bitbucket(org: String, repo: String, branch: String = GitRepoPlugin.DEFAULT_BRANCH, type: String = GitRepoPlugin.DEFAULT_TYPE): ArtifactRepository {
        return addLocalRepo(ensureLocalRepo(repositoryDir(org), repo, "git@bitbucket.org:$org/$repo.git", branch), type)
    }

    fun git(gitUrl: String, name: String, branch: String = GitRepoPlugin.DEFAULT_BRANCH, type: String = GitRepoPlugin.DEFAULT_TYPE): ArtifactRepository {
        return addLocalRepo(ensureLocalRepo(repositoryDir(name), name, gitUrl, branch), type)
    }

    internal fun repositoryDir(name: String): File {
        return if (project.hasProperty("gitRepoHome")) {
            project.file("${project.property("gitRepoHome")}/$name")
        } else {
            project.file("${System.getProperty("user.home")}/.gitRepos/$name")
        }
    }

    internal fun ensureLocalRepo(directory: File, name: String, gitUrl: String, branch: String): File {
        val repoDir = File(directory, name)
        if (project.gradle.startParameter.isOffline) return repoDir
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
        return project.repositories.maven(Action { repo ->
            repo.url = File(repoDir, type).toURI()
        })
    }
}
