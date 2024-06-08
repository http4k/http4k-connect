dependencies {
    api(project(":http4k-connect-amazon-s3"))
//    api(project(":http4k-connect-openai"))
    api("dev.langchain4j:langchain4j-core:_")
//    api("dev.langchain4j:langchain4j-open-ai:_")

//    testImplementation(project(":http4k-connect-openai-fake"))
    testImplementation(project(":http4k-connect-amazon-s3-fake"))
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
