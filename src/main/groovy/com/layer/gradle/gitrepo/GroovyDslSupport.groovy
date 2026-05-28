package com.layer.gradle.gitrepo

import org.gradle.api.artifacts.dsl.RepositoryHandler

class GroovyDslSupport {

    static void injectRepositoryMethods(RepositoryHandler repositories, Object ext) {
        if (!repositories.metaClass.respondsTo(repositories, 'github', String, String, String, String, String)) {
            repositories.metaClass.github = { String org, String repo, String branch = GitRepoPlugin.DEFAULT_BRANCH, String type = GitRepoPlugin.DEFAULT_TYPE, String name = 'github' ->
                ext.github(org, repo, branch, type, name)
            }
        }
        if (!repositories.metaClass.respondsTo(repositories, 'bitbucket', String, String, String, String, String)) {
            repositories.metaClass.bitbucket = { String org, String repo, String branch = GitRepoPlugin.DEFAULT_BRANCH, String type = GitRepoPlugin.DEFAULT_TYPE, String name = 'bitbucket' ->
                ext.bitbucket(org, repo, branch, type, name)
            }
        }
        if (!repositories.metaClass.respondsTo(repositories, 'git', String, String, String, String, String)) {
            repositories.metaClass.git = { String gitUrl, String name, String branch = GitRepoPlugin.DEFAULT_BRANCH, String type = GitRepoPlugin.DEFAULT_TYPE, String mavenName = name ->
                ext.git(gitUrl, name, branch, type, mavenName)
            }
        }
    }
}
