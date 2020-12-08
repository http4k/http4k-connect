dependencies {
    implementation(project(":http4k-connect-amazon-s3"))
    implementation(project(":http4k-connect-amazon-s3-fake"))
    implementation(project(path: ":http4k-connect-amazon-s3", configuration: "testArtifacts"))
    implementation("org.http4k:http4k-format-moshi")
}
