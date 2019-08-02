plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

android {
    compileSdkVersion(Version.AndroidSdk.compile)

    defaultConfig {
        applicationId = "com.example.bottomnavigator"
        minSdkVersion(Version.AndroidSdk.min)
        targetSdkVersion(Version.AndroidSdk.target)

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {
    implementation(project(":lib"))

    implementation(Lib.kotlinStdLib)
    implementation(Lib.appCompat)
    implementation(Lib.materialComponents)

    androidTestImplementation(Lib.espressoCore)
    androidTestImplementation(Lib.androidxTestRules)
    androidTestImplementation(Lib.androidxTestRunner)
}
