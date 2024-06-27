dependencies {
    implementation(Libs.http4k_template_handlebars)
    testImplementation("software.amazon.awssdk:s3:_")
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
