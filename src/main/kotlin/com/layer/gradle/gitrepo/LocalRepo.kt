package com.layer.gradle.gitrepo

import java.io.File

data class LocalRepo(
    val directory: File,
    val name: String,
    val gitUrl: String,
    val branch: String
)
