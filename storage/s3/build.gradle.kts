dependencies {
    implementation(project(":http4k-connect-amazon-s3"))
    implementation("org.http4k:http4k-format-moshi")
    testImplementation(project(path = ":http4k-connect-amazon-s3", configuration = "testArtifacts"))
    testImplementation(project(":http4k-connect-amazon-s3-fake"))
}
