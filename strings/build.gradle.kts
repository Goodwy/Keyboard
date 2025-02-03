plugins {
    alias(libs.plugins.agp.library)
}

val projectCompileSdk: String by project

android {
    namespace = "com.goodwy.keyboard.strings"
    compileSdk = projectCompileSdk.toInt()
}

dependencies {
    // none
}
