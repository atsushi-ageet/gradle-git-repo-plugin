import org.gradle.api.publish.maven.MavenPublication
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    groovy
    `maven-publish`
    `java-gradle-plugin`
    kotlin("jvm") version "2.3.21"
}

group = "com.layer"
apply(from = "$rootDir/version.gradle.kts")

repositories {
    mavenCentral()
}

dependencies {
    implementation(localGroovy())
    implementation("org.ajoberstar.grgit:grgit-core:5.3.3")
}

tasks.named<GroovyCompile>("compileGroovy") {
    classpath = sourceSets.main.get().compileClasspath
}

tasks.named<KotlinCompile>("compileKotlin") {
    libraries.from(sourceSets.main.get().groovy.classesDirectory)
}

gradlePlugin {
    plugins {
        create("gitRepo") {
            id = "git-repo"
            implementationClass = "com.layer.gradle.gitrepo.GitRepoPlugin"
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
