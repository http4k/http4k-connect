dependencies {
    api(project(":http4k-connect-ai-core"))

    implementation(Libs.api)

    testApi(Libs.http4k_cloudnative)
    testApi(Libs.http4k_format_moshi)
    testApi(project(path = ":http4k-connect-core", configuration = "testArtifacts"))
}
