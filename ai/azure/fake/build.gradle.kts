dependencies {
    api(project(":http4k-connect-ai-azure"))
    api(Http4k.template.pebble)
    api("org.http4k:http4k-contract-ui-swagger")
    implementation("de.sven-jacobs:loremipsum:_")
}
