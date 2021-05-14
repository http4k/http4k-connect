dependencies {
    implementation("org.http4k:http4k-serverless-lambda")

    testImplementation("com.amazonaws:aws-lambda-java-events:3.8.0")
    testImplementation("org.http4k:http4k-format-moshi")
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
