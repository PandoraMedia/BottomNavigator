buildscript {
    repositories {
        mavenCentral()
        google()
        @Suppress("UnstableApiUsage")
        gradlePluginPortal()
        jcenter()
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
        jcenter()
        google()
    }
}
