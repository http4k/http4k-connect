dependencies {
    implementation("org.http4k:http4k-template-handlebars")
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
