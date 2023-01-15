dependencies {
    api(project(":http4k-connect-amazon-core"))

   
    ksp(Libs.se_ansman_kotshi_compiler)

    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
