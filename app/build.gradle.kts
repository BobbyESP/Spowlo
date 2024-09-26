import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hilt)
}

val localProperties = Properties().apply {
    load(project.rootDir.resolve("local.properties").inputStream())
}

android {
    namespace = "com.bobbyesp.spowlo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bobbyesp.spowlo"
        minSdk = 24
        targetSdk = 34

        versionCode = rootProject.extra["versionCode"] as Int
        versionName = rootProject.extra["versionName"] as String

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        manifestPlaceholders["redirectHostName"] = "spowlo"
        manifestPlaceholders["redirectSchemeName"] = "spowlo"
    }

    buildTypes {
        release {
            buildConfigField(
                "String", "CLIENT_ID", "\"${localProperties.getProperty("CLIENT_ID")}\""
            )
            buildConfigField(
                "String", "CLIENT_SECRET", "\"${localProperties.getProperty("CLIENT_SECRET")}\""
            )
            buildConfigField(
                "String", "SPOTIFY_REDIRECT_URI_PKCE", "\"spowlo://spotify-pkce\""
            )
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            buildConfigField(
                "String", "CLIENT_ID", "\"${localProperties.getProperty("CLIENT_ID")}\""
            )
            buildConfigField(
                "String", "CLIENT_SECRET", "\"${localProperties.getProperty("CLIENT_SECRET")}\""
            )
            buildConfigField(
                "String", "SPOTIFY_REDIRECT_URI_PKCE", "\"spowlo://spotify-pkce\""
            )
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xcontext-receivers",
            "-XXLanguage:+ExplicitBackingFields"
        )
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

ksp {
    arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
}

dependencies {
    implementation(project(":app:utilities"))

    //---------------Core----------------//
    implementation(libs.bundles.core) //⚠️ This contains core kotlinx libraries, lifecycle runtime and Activity Compose support

    //---------------User Interface---------------//
    //Core UI libraries
    implementation(platform(libs.compose.bom.canary))
    implementation(libs.bundles.compose)
    implementation(libs.compose.material3.adaptive.navigation)
    implementation(libs.compose.material3.adaptive.navigation.suite)
    implementation(libs.compose.ui.utilities)
    implementation(project(":app:ui"))
    implementation(libs.bundles.accompanist)
    implementation(libs.palette)
    api(libs.material)

    //---------------Pagination---------------//
    implementation(libs.bundles.pagination)

    //-------------------Network-------------------//
    implementation(libs.bundles.ktor)

    //---------------Media3---------------//
    implementation(libs.bundles.media3)

    //---------------Dependency Injection---------------//
    implementation(libs.bundles.hilt)
    implementation(libs.androidx.media3.datasource.okhttp)
    ksp(libs.hilt.ext.compiler)
    ksp(libs.hilt.compiler)

    //-------------------Database-------------------//
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    annotationProcessor(libs.room.compiler)

    //-------------------Key-value Storage-------------------//
    implementation(libs.mmkv)

    //-------------------Markdown-------------------//
    implementation(libs.markdown)

    //-------------------Image Loading-------------------//
    implementation(libs.landscapist.coil)

    //-------------------Youtube-------------------//
//    implementation(project(":innertube"))

    //-------------------Utilities-------------------//
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.qrcode.kotlin.android)
    implementation(libs.spotify.api.android)
    implementation(libs.chrome.customTabs)
    implementation(libs.profileinstaller)

    //-------------------Testing-------------------//
    //Android testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Compose testing and tooling libraries
    androidTestImplementation(platform(libs.compose.bom.canary))
    androidTestImplementation(libs.compose.test.junit4)
    implementation(libs.compose.tooling.preview)
    debugImplementation(libs.compose.tooling)
    debugImplementation(libs.compose.test.manifest)
}

class RoomSchemaArgProvider(
    @get:InputDirectory @get:PathSensitive(PathSensitivity.RELATIVE) val schemaDir: File
) : CommandLineArgumentProvider {

    override fun asArguments(): Iterable<String> {
        if (!schemaDir.exists()) {
            schemaDir.mkdirs()
        }
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}