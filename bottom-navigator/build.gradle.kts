plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka-android")
}

android {
    compileSdkVersion(Version.AndroidSdk.compile)

    defaultConfig {
        minSdkVersion(Version.AndroidSdk.min)
        targetSdkVersion(Version.AndroidSdk.target)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(Lib.kotlinStdLib)
    implementation("androidx.activity:activity:1.1.0-alpha02")
    implementation("androidx.fragment:fragment:1.2.0-alpha02")
    implementation(Lib.materialComponents)
    implementation(Lib.lifecycleViewModel)
    implementation(Lib.lifecycleExtensions)
    api(Lib.rxjava2)
    implementation(Lib.rxjava2Extensions, Lib.rxJava2ExtensionsExcludes())
    implementation(Lib.lifecycleViewModelSavedState)

    testImplementation(Lib.junit)
    testImplementation(Lib.kotlinTest)
    testImplementation(Lib.mockitoKotlin)
}