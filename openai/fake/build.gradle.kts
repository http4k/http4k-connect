dependencies {
    api(project(":http4k-connect-openai-plugin"))
    api(Http4k.template.handlebars)
    api("org.http4k:http4k-contract-ui-swagger:_")
    implementation("de.sven-jacobs:loremipsum:_")
}
