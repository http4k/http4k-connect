dependencies {
    api("org.http4k:http4k-aws")
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
