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
    }
}

allprojects {
    repositories {
        jcenter()
        google()
    }
}
