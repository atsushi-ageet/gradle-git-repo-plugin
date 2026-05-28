[![](https://jitpack.io/v/atsushi-ageet/gradle-git-repo-plugin.svg)](https://jitpack.io/#atsushi-ageet/gradle-git-repo-plugin)

# The Gradle Git Repo Plugin

> This is a fork of the original gradle-git-repo-plugin by Layer.

This plugin allows you to add a git repository as a maven repo, even if the git
repository is private, similar to how CocoaPods works.

Using a github repo as a maven repo is a quick and easy way to host maven jars.
Private maven repos however, aren't easily accessible via the standard maven
http interface, or at least I haven't figured out how to get the authentication
right. This plugin simply clones the repo behind the scenes and uses it as a
local repo, so if you have permissions to clone the repo you can access it.

This plugin lets you tie access to your repository to github accounts, or any git repository
seamlessly. This is most useful if you've already set up to manage distribution
this way. Deliver CocoaPods and Maven artifacts with the same system, then sit
back and relax.

## Plugins

| Plugin ID | Use case |
|---|---|
| `com.ageet.git-repo` | Add git-hosted Maven repos in a project-level `repositories` block |
| `com.ageet.git-repo-settings` | Add git-hosted Maven repos via `dependencyResolutionManagement` (settings-level) |
| `com.ageet.git-repo-publish` | Publish and push artifacts to a git-hosted Maven repo |

## Setup

### Gradle Plugin Portal

No additional setup required. Plugins are resolved automatically by their ID.

### JitPack

Add the following to `settings.gradle.kts`:

```kotlin
pluginManagement {
    repositories {
        maven { url = uri("https://jitpack.io") }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("com.ageet.git-repo")) {
                useModule("com.github.atsushi-ageet:gradle-git-repo-plugin:${requested.version}")
            }
        }
    }
}
```

## Usage

### Depending on git repos — project level

Apply the plugin in `build.gradle.kts`:

```kotlin
plugins {
    id("com.ageet.git-repo") version "3.0.0"
}

repositories {
    github("myorg", "maven-private")
    bitbucket("myorg", "maven-private")
    git("git@github.com:myorg/maven-private.git", "maven-private")
}
```

`build.gradle` (Groovy DSL):

```groovy
plugins {
    id 'com.ageet.git-repo' version '3.0.0'
}

repositories {
    github 'myorg', 'maven-private'
}
```

### Depending on git repos — settings level

Using `dependencyResolutionManagement` makes repositories available to all subprojects. Apply the plugin in `settings.gradle.kts`:

```kotlin
plugins {
    id("com.ageet.git-repo-settings") version "3.0.0"
}

dependencyResolutionManagement {
    repositories {
        github("myorg", "maven-private")
        mavenCentral()
    }
}
```

### Publishing to github repos

Apply `com.ageet.git-repo-publish` instead of `com.ageet.git-repo`. It automatically applies `com.ageet.git-repo` and checks out a local branch in the publishing repository so you can commit and push after publishing.

```kotlin
plugins {
    id("com.ageet.git-repo-publish") version "3.0.0"
    `maven-publish`
}

publishing {
    publications {
        // ...
    }
    repositories {
        github("myorg", "maven-private")
    }
}
```

Then run:

```
./gradlew publish
```

## Settings

The following gradle properties affect cloning dependencies

- **offline** when defined, no network operations will be performed, the repos will be assumed to be in place
- **gitRepoHome** the base directory for cloning git repos, ~/.gitRepos by default


## Futures

It would be nice to make publishing seamless and completely
hide the locally cloned repo. That might require reimplementing maven
publishing though. The `maven-publish` plugin isn't amenable to having its
settings messed with after it's been applied unfortunately.

After long-term use, your git repo can get very large, and cloning it becomes slow

## Credits

Douglas Rapp

- http://github.com/drapp
- http://twitter.com/platykurtic
- douglas.rapp@gmail.com

## License

The gradle git repo plugin is available under the Apache 2 License. See the LICENSE file for more info.
