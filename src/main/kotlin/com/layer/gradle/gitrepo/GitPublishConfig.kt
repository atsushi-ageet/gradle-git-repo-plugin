package com.layer.gradle.gitrepo

open class GitPublishConfig {
    var org: String = ""
    var repo: String = ""
    var provider: String = "github.com"
    var gitUrl: String = ""
    var branch: String = GitRepoPlugin.DEFAULT_BRANCH
    var home: String = "${System.getProperty("user.home")}/.gitRepos"
    var publishAndPushTask: String = "publishToGithub"
    var publishTask: String = "publish"
}
