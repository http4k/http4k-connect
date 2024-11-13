val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    implementation(Libs.http4k_format_moshi)
    implementation(Libs.values4k)
    implementation(kotlin("script-runtime"))
}
