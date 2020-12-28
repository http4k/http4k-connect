dependencies {
    implementation("org.http4k:http4k-format-moshi")
//    implementation("com.amazonaws:aws-lambda-java-events:3.7.0")
//    implementation("com.amazonaws:aws-lambda-java-core:1.2.1")

    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
