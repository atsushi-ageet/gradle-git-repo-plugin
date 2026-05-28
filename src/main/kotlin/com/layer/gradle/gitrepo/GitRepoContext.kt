package com.layer.gradle.gitrepo

import java.io.File

internal interface GitRepoContext {
    val isOffline: Boolean
    fun repositoryDir(name: String): File
}
