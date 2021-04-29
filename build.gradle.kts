buildscript {
    repositories {
        google()
        mavenCentral()
        @Suppress("UnstableApiUsage")
        gradlePluginPortal()
    }
    dependencies {
        classpath(Lib.BuildPlugin.androidGradlePlugin)
        classpath(Lib.BuildPlugin.kotlinGradle)
        classpath(Lib.BuildPlugin.gradleMavenPublish)
        classpath(Lib.BuildPlugin.dokka)
    }
}

allprojects {
    repositories {
        google()
    }
}
