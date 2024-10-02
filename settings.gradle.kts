pluginManagement {
    repositories {
        maven {
            url = uri("app/libs/maven-repo")
        }
        gradlePluginPortal()
        google()
        mavenCentral()
        maven ("https://jitpack.io")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            url = uri("app/libs/maven-repo")
        }
        google()
        mavenCentral()
        maven ("https://jitpack.io")
    }
}
rootProject.name = "Spowlo"
include (":app")
include(":color")
