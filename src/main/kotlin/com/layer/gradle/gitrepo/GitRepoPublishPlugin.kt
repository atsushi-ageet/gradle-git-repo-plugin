package com.layer.gradle.gitrepo

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.publish.PublishingExtension

class GitRepoPublishPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply(GitRepoPlugin::class.java)

        project.pluginManager.withPlugin("maven-publish") {
            val publishingRepos = project.extensions.getByType(PublishingExtension::class.java).repositories
            val publishingGitRepoExt = GitRepoExtension(ProjectGitRepoContext(project), publishingRepos, createLocalBranch = true)
            (publishingRepos as? ExtensionAware)?.extensions?.add("gitRepo", publishingGitRepoExt)
            GroovyDslSupport.injectRepositoryMethods(publishingRepos, publishingGitRepoExt)
        }
    }
}
