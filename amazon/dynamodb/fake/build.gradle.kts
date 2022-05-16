dependencies {
    implementation("dev.forkhandles:parser4k")
    implementation("com.amazonaws:DynamoDBLocal:_")
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
