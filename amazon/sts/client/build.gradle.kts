dependencies {
    api(project(":http4k-connect-amazon-core"))

    testImplementation("io.mockk:mockk:1.10.4")
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
