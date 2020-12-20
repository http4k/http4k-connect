dependencies {
    api(project(":http4k-connect-amazon-core"))
    implementation("org.http4k:http4k-format-moshi") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation("se.ansman.kotshi:api:2.3.2")
    kapt("se.ansman.kotshi:compiler:2.3.2")
    kapt(project(":http4k-connect-kapt-generator"))

    testImplementation(project(path = ":http4k-connect-core", configuration = "testArtifacts"))
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
