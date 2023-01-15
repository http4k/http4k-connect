dependencies {
    api(Libs.http4k_cloudnative)
    api(Libs.http4k_format_moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation(Libs.api)



    testApi(Libs.http4k_format_moshi)
    testImplementation(project(path = ":http4k-connect-core", configuration = "testArtifacts"))
}
