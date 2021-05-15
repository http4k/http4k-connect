dependencies {
    implementation(Libs.http4k_template_handlebars)
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
