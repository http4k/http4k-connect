dependencies {
    api(project(":http4k-connect-amazon-core"))
    api(Libs.http4k_format_moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation(Libs.api)

    ksp(Libs.se_ansman_kotshi_compiler)

    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
