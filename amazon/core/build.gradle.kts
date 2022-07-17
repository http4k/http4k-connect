dependencies {
    api(Libs.http4k_aws)

    compileOnly(Libs.http4k_format_moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    implementation(Libs.api)
    kapt("com.google.devtools.ksp:symbol-processing-api:1.5.31-1.0.1")
    kapt(Libs.se_ansman_kotshi_compiler)

    implementation(Libs.http4k_format_core)
    implementation("org.ini4j:ini4j:0.5.4")
}
