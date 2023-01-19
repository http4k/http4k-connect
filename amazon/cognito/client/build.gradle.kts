dependencies {
    api(project(":http4k-connect-amazon-core"))
    api(Libs.http4k_format_moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    api(Libs.http4k_security_oauth) {
        exclude("org.http4k", "http4k-format-moshi")
    }

    implementation(Libs.api)
    testImplementation(libs.jose4j)


    ksp(Libs.se_ansman_kotshi_compiler)

    testImplementation(project(path = ":http4k-connect-core", configuration = "testArtifacts"))
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
