import org.gradle.api.Action
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.kotlin.dsl.exclude

object Version {
    const val kotlin = "1.3.40"
    const val androidGradlePlugin = "3.3.1"
    const val rxjava = "2.2.10"
    const val appCompat = "1.0.2"
    const val materialComponents = "1.0.0"
    const val constraintLayout = "1.1.3"
    const val archComponentsLifecycle = "2.0.0"
    const val rxJava2Extensions = "0.20.10"
    const val junit = "4.12"
    const val mockitoKotlinVersion = "2.1.0"

    object AndroidSdk {
        const val min = 21
        const val compile = 28
        const val target = compile
    }

}

object Lib {

    object BuildPlugin {
        const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}"
        const val androidGradlePlugin = "com.android.tools.build:gradle:${Version.androidGradlePlugin}"
    }

    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Version.kotlin}"
    const val appCompat = "androidx.appcompat:appcompat:${Version.appCompat}"
    const val materialComponents = "com.google.android.material:material:${Version.materialComponents}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Version.constraintLayout}"
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel:${Version.archComponentsLifecycle}"
    const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Version.archComponentsLifecycle}"
    const val rxjava2 = "io.reactivex.rxjava2:rxjava:${Version.rxjava}"

    const val rxjava2Extensions = "com.github.akarnokd:rxjava2-extensions:${Version.rxJava2Extensions}"
    fun rxJava2ExtensionsExcludes() = Action<ExternalModuleDependency> { exclude("io.reactivex.rxjava2") }

    const val junit = "junit:junit:${Version.junit}"

    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Version.kotlin}"
    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Version.mockitoKotlinVersion}"
}