import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.hilt)
    kotlin("kapt")
}

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
        override fun toVersionName(): String = "${versionMajor}.${versionMinor}.${versionPatch}"
    }

    class ReleaseCandidate(
        versionMajor: Int, versionMinor: Int, versionPatch: Int, versionBuild: Int
    ) : Version(versionMajor, versionMinor, versionPatch, versionBuild) {
        override fun toVersionName(): String =
            "${versionMajor}.${versionMinor}.${versionPatch}-rc.$versionBuild"
    }
}

val currentVersion: Version = Version.Stable(
    versionMajor = 2,
    versionMinor = 0,
    versionPatch = 0,
)

val keystorePropertiesFile: File = rootProject.file("keystore.properties")

val splitApks = !project.hasProperty("noSplits")

android {
    if (keystorePropertiesFile.exists()) {
        val keystoreProperties = Properties()
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
        signingConfigs {
            getByName("debug") {
                keyAlias = keystoreProperties["keyAlias"].toString()
                keyPassword = keystoreProperties["keyPassword"].toString()
                storeFile = file(keystoreProperties["storeFile"]!!)
                storePassword = keystoreProperties["storePassword"].toString()
            }
        }
    }

    compileSdk = 34
    defaultConfig {
        applicationId = "com.bobbyesp.spowlo"
        minSdk = 24
        targetSdk = 34
        versionCode = currentVersion.run {
            versionMajor * 10000 + versionMinor * 1000 + versionPatch * 100 + versionBuild
        }

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
        if (!splitApks) ndk {
            (properties["ABI_FILTERS"] as String).split(';').forEach {
                abiFilters.add(it)
            }
        }
        manifestPlaceholders["redirectHostName"] = "spowlo"
        manifestPlaceholders["redirectSchemeName"] = "spowlo"
    }
    if (splitApks) splits {
        abi {
            isEnable = !project.hasProperty("noSplits")
            reset()
            include("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
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
            if (keystorePropertiesFile.exists()) signingConfig = signingConfigs.getByName("debug")
            //add client id and secret to build config
            buildConfigField(
                "String", "CLIENT_ID", "\"${project.properties["CLIENT_ID"]}\""
            )
            buildConfigField(
                "String", "CLIENT_SECRET", "\"${project.properties["CLIENT_SECRET"]}\""
            )
            buildConfigField(
                "String", "SPOTIFY_REDIRECT_URI_PKCE", "\"spowlo://spotify-pkce\""
            )
            matchingFallbacks.add(0, "debug")
            matchingFallbacks.add(1, "release")
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            if (keystorePropertiesFile.exists()) signingConfig = signingConfigs.getByName("debug")
            packaging {
                resources.excludes.add("META-INF/*.kotlin_module")
            }
            buildConfigField("String", "CLIENT_ID", "\"${project.properties["CLIENT_ID"]}\"")
            buildConfigField(
                "String", "CLIENT_SECRET", "\"${project.properties["CLIENT_SECRET"]}\""
            )
            buildConfigField(
                "String", "SPOTIFY_REDIRECT_URI_PKCE", "\"spowlo://spotify-pkce\""
            )
            matchingFallbacks.add(0, "debug")
            matchingFallbacks.add(1, "release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
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
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/*.kotlin_module"
            excludes += "/META-INF/versions/9/previous-compilation-data.bin"
        }
        jniLibs.useLegacyPackaging = true
    }
    namespace = "com.bobbyesp.spowlo"
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {

    implementation(project(":color"))

    //Core libs for the app
    implementation(libs.bundles.core)

    //Material UI, Accompanist...
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)
    implementation(libs.bundles.accompanist)
    implementation(libs.material)
    implementation(libs.bundles.pagination)

    //Coil (For Jetpack Compose)
    implementation(libs.compose.coil)
    implementation(libs.landscapist.coil)

    //Serialization
    implementation(libs.kotlin.serialization.json)
    implementation(libs.kotlin.datetime)

    //DI (Dependency Injection - Hilt)
    implementation(libs.bundles.hilt)
    kapt(libs.bundles.hilt.kapt)

    //Database powered by Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)

    //Datastore (Preferences)
    implementation(libs.datastore.preferences)

    //Networking
    implementation(libs.bundles.ktor)

    //MMKV (Key-Value storage)
    implementation(libs.mmkv)

    //Spotify API wrapper
    implementation(libs.spotify.api.android)

    //Spotify downloader
//    implementation(libs.bundles.spotdl)
    implementation(project(":spotdl_lib"))
    implementation(project(":ffmpeg"))
    implementation(project(":spotdl_utilities"))

    //Miniplayer service
    implementation(libs.media3.session)
    implementation(project(":miniplayer_service"))

    //Chrome Custom Tabs
    implementation(libs.chrome.custom.tabs)

    //MD Parser
    implementation(libs.markdown)

    //Shimmer
    implementation(libs.shimmer)

    //BottomSheets
    implementation(libs.modalBottomSheet)

    //Metadata editor
    implementation(libs.metadata.manager)

    //Compose testing libs
    implementation(libs.compose.tooling.preview)
    debugImplementation(libs.compose.tooling)
    debugImplementation(libs.compose.test.manifest)

    //Memory leaks detection
    debugImplementation(libs.leakcanary)

}

class RoomSchemaArgProvider(
    @get:InputDirectory @get:PathSensitive(PathSensitivity.RELATIVE) val schemaDir: File
) : CommandLineArgumentProvider {

    override fun asArguments(): Iterable<String> {
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}
