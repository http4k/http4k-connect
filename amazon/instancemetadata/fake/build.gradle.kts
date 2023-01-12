dependencies {
    testImplementation(project(path = ":http4k-connect-amazon-sns-fake"))
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
