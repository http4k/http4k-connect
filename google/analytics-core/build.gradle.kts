dependencies {
    implementation(Libs.api)
    kapt("com.google.devtools.ksp:symbol-processing-api:1.5.31-1.0.1")
    kapt(Libs.se_ansman_kotshi_compiler)
}
