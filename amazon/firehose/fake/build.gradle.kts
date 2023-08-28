dependencies {
    implementation(Http4k.format.moshi)
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
