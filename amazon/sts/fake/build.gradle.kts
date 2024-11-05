dependencies {
    implementation(Libs.http4k_template_pebble)

    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
    testImplementation(project(":http4k-connect-amazon-sqs"))
}
