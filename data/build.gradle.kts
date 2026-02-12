plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(libs.org.kotlinx.coroutines.core)
    implementation(libs.org.kotlinx.coroutines.android)
    testImplementation(libs.junit)
}
