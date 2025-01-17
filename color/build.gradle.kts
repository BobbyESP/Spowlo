plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.compose.compiler)
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    jvmToolchain(21)
}
android {
    compileSdk = 35
    defaultConfig {
        minSdk = 21
    }
    namespace = "com.bobbyesp.spowlo.color"
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        sourceCompatibility = JavaVersion.VERSION_17
    }
    buildTypes {
        all {
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            isMinifyEnabled = false
        }
    }
}
dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.ui)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.core.ktx)
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.material3)
}