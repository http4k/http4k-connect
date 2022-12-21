dependencies {
    api(Http4k.securityOauth)
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
