dependencies {
    api(project(":http4k-connect-amazon-core"))
    api(Libs.http4k_format_moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation(Libs.api)
    kapt("com.google.devtools.ksp:symbol-processing-api:1.5.31-1.0.1")
    kapt(Libs.se_ansman_kotshi_compiler)

    testImplementation("org.jetbrains.kotlin", "kotlin-reflect")
    testImplementation(Libs.http4k_serverless_lambda)
    testImplementation(project(path = ":http4k-connect-core", configuration = "testArtifacts"))
    testImplementation(project(path = ":http4k-connect-core", configuration = "testArtifacts"))
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
    testApi(project(path = ":http4k-connect-amazon-s3"))
    testImplementation(platform(Libs.bom))
    testImplementation("software.amazon.awssdk:dynamodb")
}
