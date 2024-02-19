dependencies {
    api(project(":http4k-connect-amazon-s3"))
    implementation(Libs.http4k_format_moshi)
    testImplementation(project(path = ":http4k-connect-amazon-s3", configuration = "testArtifacts"))
    testImplementation(project(":http4k-connect-amazon-s3-fake"))
}
