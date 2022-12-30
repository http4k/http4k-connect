dependencies {
    implementation(libs.parser4k)
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
    testImplementation(project(path = ":http4k-connect-amazon-s3"))
}
