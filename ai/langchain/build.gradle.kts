dependencies {
    api(project(":http4k-connect-amazon-s3"))
    api(project(":http4k-connect-ai-openai"))
    api(project(":http4k-connect-ai-ollama"))
    api(project(":http4k-connect-ai-lmstudio"))
    api("dev.langchain4j:langchain4j-core:_")

    testImplementation("dev.langchain4j:langchain4j:_")
    testImplementation(project(":http4k-connect-ai-openai-fake"))
    testImplementation(project(":http4k-connect-ai-ollama-fake"))
    testImplementation(project(":http4k-connect-ai-lmstudio-fake"))
    testImplementation(project(":http4k-connect-amazon-s3-fake"))
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
