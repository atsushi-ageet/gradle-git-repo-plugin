package com.layer.gradle.gitrepo

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

class GroovyDslSupport {
    static final List<String> REPOSITORY_METHODS = ['github', 'bitbucket', 'git'].asImmutable()

    static void injectRepositoryMethods(Project project) {
        REPOSITORY_METHODS.each { String methodName ->
            injectMethod(project, methodName)
        }
    }

    private static void injectMethod(Project project, String methodName) {
        if (!project.repositories.metaClass.respondsTo(project.repositories, methodName, String, String, String, String)) {
            project.repositories.metaClass."$methodName" = { String arg1, String arg2, String branch = GitRepoPlugin.DEFAULT_BRANCH, String type = GitRepoPlugin.DEFAULT_TYPE ->
                ((ExtensionAware) project.repositories).extensions.getByName('gitRepo')."$methodName"(arg1, arg2, branch, type)
            }
        }
    }
}
