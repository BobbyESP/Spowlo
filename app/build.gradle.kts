import java.io.FileInputStream
import java.util.Locale
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization")
    alias(libs.plugins.compose.compiler)
}
apply(plugin = "dagger.hilt.android.plugin")

sealed class Version(
    open val versionMajor: Int,
    val versionMinor: Int,
    val versionPatch: Int,
    val versionBuild: Int = 0
) {
    abstract fun toVersionName(): String

    fun toVersionCode(): Int =
        versionMajor * 1000000 + versionMinor * 10000 + versionPatch * 100 + versionBuild

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
    versionMajor = 1,
    versionMinor = 5,
    versionPatch = 3,
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

    val localProperties = Properties()
    localProperties.load(FileInputStream(rootProject.file("local.properties")))

    compileSdk = 35
    defaultConfig {
        applicationId = "com.bobbyesp.spowlo"
        minSdk = 26
        targetSdk = 35
        versionCode = currentVersion.toVersionCode()

        versionName = currentVersion.toVersionName().run {
            if (!splitApks) "$this-(F-Droid)"
            else this
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
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
                include("arm64-v8a", "armeabi-v7a")
                isUniversalApk = false
            }
        }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            packaging {
                resources.excludes.add("META-INF/*.kotlin_module")
            }
            if (keystorePropertiesFile.exists())
                signingConfig = signingConfigs.getByName("debug")
            //add client id and secret to build config
            buildConfigField("String", "CLIENT_ID", "\"${localProperties["CLIENT_ID"]}\"")
            buildConfigField(
                "String",
                "CLIENT_SECRET",
                "\"${localProperties["CLIENT_SECRET"]}\""
            )
            matchingFallbacks.add(0, "debug")
            matchingFallbacks.add(1, "release")
        }
        debug {
            if (keystorePropertiesFile.exists())
                signingConfig = signingConfigs.getByName("debug")
            packaging {
                resources.excludes.add("META-INF/*.kotlin_module")
            }
            buildConfigField("String", "CLIENT_ID", "\"${localProperties["CLIENT_ID"]}\"")
            buildConfigField(
                "String",
                "CLIENT_SECRET",
                "\"${localProperties["CLIENT_SECRET"]}\""
            )
            System.setProperty("CLIENT_ID", "\"${localProperties["CLIENT_ID"]}\"")
            System.setProperty("CLIENT_SECRET", "\"${localProperties["CLIENT_SECRET"]}\"")
            matchingFallbacks.add(0, "debug")
            matchingFallbacks.add(1, "release")
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "Spowlo (Debug)")
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/*.kotlin_module"
        }
        jniLibs.useLegacyPackaging = true
    }
    namespace = "com.bobbyesp.spowlo"
}

kotlin {
    jvmToolchain(21)
}

dependencies {

    implementation(project(":color"))
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

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.accompanist.webview)
    implementation(libs.accompanist.flowlayout)
    implementation(libs.accompanist.material)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.paging.compose)
    implementation(libs.paging.runtime)

    implementation(libs.coil.kt.compose)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.ext.compiler)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    //spotDL library
    implementation(libs.spotdl.android.library)
    implementation(libs.spotdl.android.ffmpeg)

    implementation(libs.spotify.api.android)

    // okhttp
    implementation(libs.okhttp)
    implementation(libs.bundles.ktor)
    //MMKV
    implementation(libs.mmkv)

    implementation(libs.markdown)
    implementation(libs.customtabs)

    debugImplementation(libs.crash.handler)

    testImplementation(libs.junit4)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
//    androidTestImplementation(libs.androidx.compose.ui.test)

    debugImplementation(libs.androidx.compose.ui.tooling)
}

fun String.capitalizeWord(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }
}

class RoomSchemaArgProvider(
    @get:InputDirectory @get:PathSensitive(PathSensitivity.RELATIVE) val schemaDir: File
) : CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> {
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}