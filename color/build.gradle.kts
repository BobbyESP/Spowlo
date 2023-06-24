plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

android {
    compileSdk = 34
    defaultConfig {
        minSdk = 21
        targetSdk = 34
    }
    namespace = "com.bobbyesp.spowlo.color"
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    buildFeatures {
        compose = true
    }
    buildTypes {
        debug {
            isMinifyEnabled = true
        }
        release {
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}
dependencies {
    api(platform(libs.compose.bom))
    api(libs.compose.ui)
    api(libs.compose.runtime)
    api(libs.core.ktx)
    api(libs.compose.foundation)
    api(libs.compose.material3)
}