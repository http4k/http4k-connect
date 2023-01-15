dependencies {
    api(Libs.http4k_aws)

    compileOnly(Libs.http4k_format_moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    implementation(Libs.api)
   
    kapt(Libs.se_ansman_kotshi_compiler)

    implementation(Libs.http4k_format_core)
    implementation(libs.ini4j)
}
