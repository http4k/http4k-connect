dependencies {
    api(project(":http4k-connect-amazon-s3"))
    testImplementation(project(":http4k-connect-amazon-s3-fake"))
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
