dependencies {
    implementation("dev.forkhandles:parser4k")
    api("com.amazonaws:DynamoDBLocal:1.13.6")
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}

