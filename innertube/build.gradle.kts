plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.brotli)
    testImplementation(libs.junit)
}