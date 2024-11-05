dependencies {
    implementation(Libs.http4k_template_pebble)
    implementation(Libs.http4k_format_moshi)

    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
