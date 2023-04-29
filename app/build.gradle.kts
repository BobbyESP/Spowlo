import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization")
}
apply(plugin = "dagger.hilt.android.plugin")

sealed class Version(
    open val versionMajor: Int,
    val versionMinor: Int,
    val versionPatch: Int,
    val versionBuild: Int = 0
) {
    abstract fun toVersionName(): String
    class Beta(versionMajor: Int, versionMinor: Int, versionPatch: Int, versionBuild: Int) :
        Version(versionMajor, versionMinor, versionPatch, versionBuild) {
        override fun toVersionName(): String =
            "${versionMajor}.${versionMinor}.${versionPatch}-beta.$versionBuild"
    }

    class Stable(versionMajor: Int, versionMinor: Int, versionPatch: Int) :
        Version(versionMajor, versionMinor, versionPatch) {
        override fun toVersionName(): String =
            "${versionMajor}.${versionMinor}.${versionPatch}"
    }

    class ReleaseCandidate(
        versionMajor: Int,
        versionMinor: Int,
        versionPatch: Int,
        versionBuild: Int
    ) :
        Version(versionMajor, versionMinor, versionPatch, versionBuild) {
        override fun toVersionName(): String =
            "${versionMajor}.${versionMinor}.${versionPatch}-rc.$versionBuild"
    }
}

val currentVersion: Version = Version.Stable(
    versionMajor = 2,
    versionMinor = 0,
    versionPatch = 0,
)

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
        minSdk = 26
        targetSdk = 33
        versionCode = 20000

        versionName = currentVersion.toVersionName().run {
            if (!splitApks) "$this-(F-Droid)"
            else this
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
            correctErrorTypes = true
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
            packagingOptions {
                resources.excludes.add("META-INF/*.kotlin_module")
            }
            if (keystorePropertiesFile.exists())
                signingConfig = signingConfigs.getByName("debug")
            //add client id and secret to build config
            buildConfigField("String", "CLIENT_ID", "\"${project.properties["CLIENT_ID"]}\"")
            buildConfigField(
                "String",
                "CLIENT_SECRET",
                "\"${project.properties["CLIENT_SECRET"]}\""
            )
            matchingFallbacks.add(0, "debug")
            matchingFallbacks.add(1, "release")
        }
        debug {
            if (keystorePropertiesFile.exists())
                signingConfig = signingConfigs.getByName("debug")
            packagingOptions {
                resources.excludes.add("META-INF/*.kotlin_module")
            }
            buildConfigField("String", "CLIENT_ID", "\"${project.properties["CLIENT_ID"]}\"")
            buildConfigField(
                "String",
                "CLIENT_SECRET",
                "\"${project.properties["CLIENT_SECRET"]}\""
            )
            matchingFallbacks.add(0, "debug")
            matchingFallbacks.add(1, "release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    kotlin {
        jvmToolchain(11)
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
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/*.kotlin_module"
        }
        jniLibs.useLegacyPackaging = true
    }
    namespace = "com.bobbyesp.spowlo"
}

dependencies {

    implementation(project(":color"))
    implementation(project(":uisdk"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.lifecycle.viewModelCompose)

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.iconsExtended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.foundation)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.bundles.accompanist)
    implementation(libs.paging.compose)
    implementation(libs.paging.runtime)

    implementation(libs.coil.kt.compose)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.hilt.ext.compiler)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    //spotDL library
    implementation(libs.spotdl.android.library)
    implementation(libs.spotdl.android.ffmpeg)

    //Spotify API wrapper
    implementation(libs.spotify.api.android)

    // okhttp
    implementation(libs.okhttp)

    //MMKV
    implementation(libs.mmkv)

    //markdown pasrser
    implementation(libs.markdown)

    //Exoplayer
    //implementation(libs.bundles.exoplayer)

    implementation(libs.customtabs)
   // implementation(libs.shimmer)

    debugImplementation(libs.crash.handler)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
//    androidTestImplementation(libs.androidx.compose.ui.test)

    debugImplementation(libs.androidx.compose.ui.tooling)

}
