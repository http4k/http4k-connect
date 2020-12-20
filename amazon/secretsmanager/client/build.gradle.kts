dependencies {
    api(project(":http4k-connect-amazon-core"))
    implementation("org.http4k:http4k-format-moshi")
    kapt("se.ansman.kotshi:compiler:2.3.2")
    implementation("se.ansman.kotshi:api:2.3.2")

    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
