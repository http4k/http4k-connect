dependencies {
    api("org.http4k:http4k-format-moshi") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation("se.ansman.kotshi:api:2.3.3")
    kapt("se.ansman.kotshi:compiler:2.3.3")

    testImplementation(project(path = ":http4k-connect-core", configuration = "testArtifacts"))
}
