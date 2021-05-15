dependencies {
    api("org.http4k:http4k-cloudnative")
    api("org.http4k:http4k-format-moshi") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation(Libs.api)
    kapt(Libs.se_ansman_kotshi_compiler)

    testApi("org.http4k:http4k-format-moshi")
    testImplementation(project(path = ":http4k-connect-core", configuration = "testArtifacts"))
}
