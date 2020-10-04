dependencies {
    implementation("org.http4k:http4k-aws")
    implementation("org.http4k:http4k-client-okhttp")
    testImplementation(project(":http4k-connect-amazon-s3-fake"))
}
