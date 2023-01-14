dependencies {
    implementation(Libs.http4k_format_moshi)
    implementation(Libs.kotlinpoet)
    implementation(Libs.kotlinpoet_metadata)
    implementation(libs.symbol.processing.api)

    kspTest(project)
}
