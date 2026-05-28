import org.gradle.api.publish.maven.MavenPublication
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    groovy
    `maven-publish`
    `java-gradle-plugin`
    kotlin("jvm") version "2.3.21"
    id("com.gradle.plugin-publish") version "1.3.1"
}

group = "com.ageet.gradle-git-repo-plugin"
apply(from = "$rootDir/version.gradle.kts")

repositories {
    mavenCentral()
}

dependencies {
    implementation(localGroovy())
    implementation("org.ajoberstar.grgit:grgit-core:5.3.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.named<GroovyCompile>("compileGroovy") {
    classpath = sourceSets.main.get().compileClasspath
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

tasks.named<KotlinCompile>("compileKotlin") {
    libraries.from(sourceSets.main.get().groovy.classesDirectory)
}

gradlePlugin {
    website = "https://github.com/atsushi-ageet/gradle-git-repo-plugin"
    vcsUrl = "https://github.com/atsushi-ageet/gradle-git-repo-plugin"

    plugins {
        create("gitRepo") {
            id = "com.ageet.git-repo"
            implementationClass = "com.layer.gradle.gitrepo.GitRepoPlugin"
            displayName = "Git Repo Plugin"
            description = "Use a git repository as a Maven repository"
            tags = listOf("git", "maven", "repository", "github", "bitbucket")
        }
        create("gitRepoSettings") {
            id = "com.ageet.git-repo-settings"
            implementationClass = "com.layer.gradle.gitrepo.GitRepoSettingsPlugin"
            displayName = "Git Repo Settings Plugin"
            description = "Use a git repository as a Maven repository via dependencyResolutionManagement"
            tags = listOf("git", "maven", "repository", "github", "bitbucket")
        }
        create("gitRepoPublish") {
            id = "com.ageet.git-repo-publish"
            implementationClass = "com.layer.gradle.gitrepo.GitRepoPublishPlugin"
            displayName = "Git Repo Publish Plugin"
            description = "Publish and push Maven artifacts to a git repository"
            tags = listOf("git", "maven", "publish", "github", "bitbucket")
        }
    }
}

publishing {
    repositories {
        maven {
            name = "localPluginRepository"
            url = uri("local-plugin-repository")
        }
    }
}

afterEvaluate {
    publishing {
        publications.named<MavenPublication>("pluginMaven") {
            artifactId = "git-repo-plugin"
        }
    }
}
