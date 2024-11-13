import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
}

dependencies {
    api(project(":http4k-connect-ai-azure"))
    api(Http4k.template.pebble)
    api("org.http4k:http4k-contract-ui-swagger")
    implementation("de.sven-jacobs:loremipsum:_")
}
