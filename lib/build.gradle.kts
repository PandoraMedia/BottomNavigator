plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
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
    implementation(Lib.appCompat)
    implementation(Lib.materialComponents)
    implementation(Lib.lifecycleViewModel)
    implementation(Lib.lifecycleExtensions)
    api(Lib.rxjava2)

    implementation(Lib.rxjava2Extensions, Lib.rxJava2ExtensionsExcludes())
    testImplementation(Lib.junit)
    testImplementation(Lib.kotlinTest)
    testImplementation(Lib.mockitoKotlin)
}
