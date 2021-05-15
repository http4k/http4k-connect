dependencies {
    testImplementation(Libs.aws_lambda_java_events)
    testImplementation("org.http4k:http4k-format-moshi")
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
