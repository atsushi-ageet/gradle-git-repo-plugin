package com.layer.gradle.gitrepo

import org.gradle.api.artifacts.dsl.RepositoryHandler

class GroovyDslSupport {
    static final List<String> REPOSITORY_METHODS = ['github', 'bitbucket', 'git'].asImmutable()

    static void injectRepositoryMethods(RepositoryHandler repositories, Object ext) {
        REPOSITORY_METHODS.each { String methodName ->
            injectMethod(repositories, ext, methodName)
        }
    }

    private static void injectMethod(RepositoryHandler repositories, Object ext, String methodName) {
        if (!repositories.metaClass.respondsTo(repositories, methodName, String, String, String, String)) {
            repositories.metaClass."$methodName" = { String arg1, String arg2, String branch = GitRepoPlugin.DEFAULT_BRANCH, String type = GitRepoPlugin.DEFAULT_TYPE ->
                ext."$methodName"(arg1, arg2, branch, type)
            }
        }
    }
}
