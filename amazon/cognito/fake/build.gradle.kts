dependencies {
    api(Http4k.securityOauth)
    implementation(libs.jose4j)

    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
