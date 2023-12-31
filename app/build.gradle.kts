plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.ksp)
//    alias(libs.plugins.google.gms)
    alias(libs.plugins.hilt)
//    id("com.google.firebase.crashlytics")
    alias(libs.plugins.androidx.baselineprofile)
}

sealed class Version(
    open val versionMajor: Int,
    val versionMinor: Int,
    val versionPatch: Int,
    val versionBuild: Int = 0
) {
    abstract fun toVersionName(): String
    fun toVersionCode(): Int {
        val minorExtraDigit = if (versionMinor > 9) {
            (versionMinor / 10).toString()
        } else {
            ""
        }

        return "$versionMajor$minorExtraDigit$versionPatch$versionBuild".toInt()
    }

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
    versionMajor = 0,
    versionMinor = 0,
    versionPatch = 1
)


android {
    namespace = "com.bobbyesp.spowlo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bobbyesp.spowlo"
        minSdk = 24
        targetSdk = 34

        versionCode = currentVersion.toVersionCode()
        versionName = currentVersion.toVersionName().run {
            this
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ksp {
            arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":color"))
    implementation(project(":app:ui"))
    implementation(project(":app:utilities"))
    //---------------Core----------------//
    implementation(libs.bundles.core) //⚠️ This contains core kotlinx libraries, lifecycle runtime and Activity Compose support

    //---------------User Interface---------------//
    //Core UI libraries
    implementation(platform(libs.compose.bom))

    //Accompanist libraries
    implementation(libs.bundles.accompanist)

    //Compose libraries
    implementation(libs.bundles.compose)
    implementation(libs.material)
    implementation(libs.orbital)

    //Pagination
    implementation(libs.bundles.pagination)

    //-------------------Network-------------------//
    //Ktor libraries
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

    //-------------------FIREBASE-------------------//
//    implementation(platform(libs.firebase.bom))
//    implementation(libs.firebase.analytics)
//    implementation(libs.firebase.crashlytics)

    //-------------------Youtube-------------------//
    implementation(project(":innertube"))

    //-------------------Utilities-------------------//
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.qrcode.kotlin.android)
    implementation(libs.profileinstaller)
    implementation(libs.kotlinx.datetime)

    //-------------------Testing-------------------//
    //Android testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Compose testing and tooling libraries
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.test.junit4)
    implementation(libs.compose.tooling.preview)
    debugImplementation(libs.compose.tooling)
    debugImplementation(libs.compose.test.manifest)
}

class RoomSchemaArgProvider(
    @get:InputDirectory @get:PathSensitive(PathSensitivity.RELATIVE) val schemaDir: File
) : CommandLineArgumentProvider {

    override fun asArguments(): Iterable<String> {
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}