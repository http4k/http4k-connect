dependencies {
    testImplementation(Libs.aws_lambda_java_events)
    testImplementation(Libs.http4k_format_moshi)
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
