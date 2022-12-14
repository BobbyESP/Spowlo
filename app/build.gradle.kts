import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "1.7.20"
}
apply(plugin = "dagger.hilt.android.plugin")

val versionMajor = 0
val versionMinor = 2
val versionPatch = 1
val versionBuild = 0
val isStable = true

val composeVersion: String by rootProject.extra
val lifecycleVersion: String by rootProject.extra
val navigationVersion: String by rootProject.extra
val roomVersion: String by rootProject.extra
val accompanistVersion: String by rootProject.extra
val composeMd3Version: String by rootProject.extra
val youtubedlAndroidVersion: String by rootProject.extra
val coilVersion: String by rootProject.extra
val okhttpVersion: String by rootProject.extra
val hiltVersion: String by rootProject.extra
val spotifyLibrary: String by rootProject.extra

val keystorePropertiesFile = rootProject.file("keystore.properties")

val splitApks = !project.hasProperty("noSplits")

android {
    if (keystorePropertiesFile.exists()) {
        val keystoreProperties = Properties()
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
        signingConfigs {
            getByName("debug")
            {
                keyAlias = keystoreProperties["keyAlias"].toString()
                keyPassword = keystoreProperties["keyPassword"].toString()
                storeFile = file(keystoreProperties["storeFile"]!!)
                storePassword = keystoreProperties["storePassword"].toString()
            }
        }
    }

    compileSdk = 33
    defaultConfig {
        applicationId = "com.bobbyesp.spowlo"
        minSdk = 23
        targetSdk = 33
        versionCode = 10000
        versionName = StringBuilder("${versionMajor}.${versionMinor}.${versionPatch}").apply {
            if (!isStable) append("-beta.${versionBuild}")
            if (!splitApks) append("-(F-Droid)")
        }.toString()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
        if (!splitApks)
            ndk {
                (properties["ABI_FILTERS"] as String).split(';').forEach {
                    abiFilters.add(it)
                }
            }
    }
    if (splitApks)
        splits {
            abi {
                isEnable = !project.hasProperty("noSplits")
                reset()
                include("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
                isUniversalApk = false
            }
        }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            if (keystorePropertiesFile.exists())
                signingConfig = signingConfigs.getByName("debug")
            buildConfigField("String",
                "SPOTIFY_CLIENT_ID",
                "\"abcad8ba647d4b0ebae797a8f444ac9b\"")
            buildConfigField("String",
                "SPOTIFY_REDIRECT_URI",
                "\"spowlo://spotify-auth\"")
            buildConfigField(
                "String",
                "SPOTIFY_REDIRECT_URI_PKCE",
                "\"spowlo://spotify-pkce\""
            )
            packagingOptions {
                resources.excludes.add("META-INF/*.kotlin_module")
            }
            matchingFallbacks.add(0, "debug")
            matchingFallbacks.add(1, "release")
        }
        debug {
            if (keystorePropertiesFile.exists())
                signingConfig = signingConfigs.getByName("debug")
            buildConfigField(
                "String",
                "SPOTIFY_CLIENT_ID",
                "\"abcad8ba647d4b0ebae797a8f444ac9b\"")

            buildConfigField(
                "String",
                "SPOTIFY_REDIRECT_URI_AUTH",
                "\"spowlo://spotify-auth\"")

            buildConfigField(
                "String",
                "SPOTIFY_REDIRECT_URI_PKCE",
                "\"spowlo://spotify-pkce\""
            )
            matchingFallbacks.add(0, "debug")
            matchingFallbacks.add(1, "release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }

    lint {
        disable.addAll(listOf("MissingTranslation", "ExtraTranslation"))
    }

    applicationVariants.all {
        outputs.all {
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "Spowlo-${defaultConfig.versionName}-${name}.apk"
        }
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        jniLibs.useLegacyPackaging = true
    }
    namespace = "com.bobbyesp.spowlo"
}

dependencies {
    implementation(project(":color"))
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.0-rc01")
    implementation("com.google.android.material:material:1.8.0-alpha02")
    implementation("androidx.activity:activity-compose:1.6.1")
    //Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    //Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
    //UI
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material3:material3:$composeMd3Version")
    implementation("androidx.compose.material3:material3-window-size-class:$composeMd3Version")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.compose.material:material-icons-extended:$composeVersion")
    implementation("androidx.navigation:navigation-compose:$navigationVersion")
    implementation("androidx.compose.animation:animation-graphics:$composeVersion")
    implementation("androidx.compose.foundation:foundation:$composeVersion")
    implementation("com.google.accompanist:accompanist-navigation-animation:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-permissions:$accompanistVersion")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanistVersion")
    implementation("io.coil-kt:coil-compose:$coilVersion")
    implementation("com.holix.android:bottomsheetdialog-compose:1.0.3")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

    implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
    implementation("androidx.browser:browser:1.4.0")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")

    //Room (Databases)
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    // Retrofit and okhttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.google.code.gson:gson:2.9.0")

    //MMKV (used for Configuration)
    implementation("com.tencent:mmkv:1.2.14")

    //SimpleStorage (SAF Simplifier)
    implementation("com.anggrayudi:storage:1.5.0")

    //Yt-dlp

    //Spotify SDK Integration library
    implementation("com.adamratzman:spotify-api-kotlin-core:$spotifyLibrary")

    //Unit testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
}
