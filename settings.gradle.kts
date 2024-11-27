pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        flatDir {
            dirs("libs")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://repository.map.naver.com/archive/maven")
        maven("https://devrepo.kakaomobility.com/repository/kakao-mobility-android-knsdk-public/")
        maven("https://www.jitpack.io")
        maven("https://devrepo.kakao.com/nexus/content/groups/public/")
    }
}

rootProject.name = "DriveMate"
include(":app")
 