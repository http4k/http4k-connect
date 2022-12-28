dependencies {
    api(project(":http4k-connect-google-analytics-core"))
    implementation(Libs.api)
    kapt(libs.symbol.processing.api)
    kapt(Libs.se_ansman_kotshi_compiler)
}
