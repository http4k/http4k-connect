dependencies {
    api(project(":http4k-connect-amazon-core"))

    testImplementation(Libs.mockk)
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
