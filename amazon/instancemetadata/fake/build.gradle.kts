dependencies {
    testImplementation(project(path = ":http4k-connect-amazon-s3"))
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
