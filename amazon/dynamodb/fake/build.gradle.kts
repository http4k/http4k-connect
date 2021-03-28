dependencies {
    testImplementation("org.testcontainers:dynalite:1.15.2")
    testImplementation("com.amazonaws:aws-java-sdk-dynamodb:1.11.986")
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
