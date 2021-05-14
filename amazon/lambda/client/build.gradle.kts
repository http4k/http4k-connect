dependencies {
    api(project(":http4k-connect-amazon-core"))
    api("org.http4k:http4k-serverless-lambda")

    testImplementation("com.amazonaws:aws-lambda-java-events:3.8.0")
    testImplementation(project(path = ":http4k-connect-core", configuration = "testArtifacts"))
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
