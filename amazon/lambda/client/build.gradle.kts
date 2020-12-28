dependencies {
    api(project(":http4k-connect-amazon-core"))
    implementation("org.http4k:http4k-format-moshi") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    testImplementation(project(path = ":http4k-connect-core", configuration = "testArtifacts"))
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
