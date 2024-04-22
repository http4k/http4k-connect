dependencies {
    implementation(Libs.http4k_template_handlebars) // TODO delete

//    testImplementation(Libs.sqs)  FIXME why doesn't this work?
    testImplementation("software.amazon.awssdk:sqs:_")
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
