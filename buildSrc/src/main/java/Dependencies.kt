/*
 * Copyright 2019 Pandora Media, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * See accompanying LICENSE file or you may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.gradle.api.Action
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.exclude

object Version {
    const val dokka = "0.9.18"
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
    const val espresso = "3.2.0"
    const val androidxTest = "1.2.0"
    const val androidxTestExt = "1.1.1"
    const val gradleMavenPublish = "0.8.0"

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
        const val gradleMavenPublish = "com.vanniktech:gradle-maven-publish-plugin:${Version.gradleMavenPublish}"
        const val dokka = "org.jetbrains.dokka:dokka-android-gradle-plugin:${Version.dokka}"
    }

    const val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Version.kotlin}"
    const val appCompat = "androidx.appcompat:appcompat:${Version.appCompat}"
    const val materialComponents = "com.google.android.material:material:${Version.materialComponents}"
    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel:${Version.archComponentsLifecycle}"
    const val lifecycleExtensions = "androidx.lifecycle:lifecycle-extensions:${Version.archComponentsLifecycle}"
    const val rxjava2 = "io.reactivex.rxjava2:rxjava:${Version.rxjava}"

    const val rxjava2Extensions = "com.github.akarnokd:rxjava2-extensions:${Version.rxJava2Extensions}"
    fun rxJava2ExtensionsExcludes() = Action<ExternalModuleDependency> { exclude("io.reactivex.rxjava2") }

    //Testing
    const val junit = "junit:junit:${Version.junit}"
    const val kotlinTest = "org.jetbrains.kotlin:kotlin-test:${Version.kotlin}"
    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${Version.mockitoKotlinVersion}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Version.espresso}"
    const val androidxTestRunner = "androidx.test.ext:junit:${Version.androidxTestExt}"
    const val androidxTestRules = "androidx.test:rules:${Version.androidxTest}"

}