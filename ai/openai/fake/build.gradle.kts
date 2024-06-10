dependencies {
    api(project(":http4k-connect-ai-openai-plugin"))
    api(Http4k.template.handlebars)
    api("org.http4k:http4k-contract-ui-swagger")
    implementation("de.sven-jacobs:loremipsum:_")
}
