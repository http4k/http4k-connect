dependencies {
    api(project(":http4k-connect-amazon-core"))
    api("org.http4k:http4k-format-moshi")

    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
