import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
    id("org.http4k.connect.fake")
}

dependencies {
    api(project(":http4k-connect-ai-azure"))
    api("org.http4k:http4k-template-pebble:${rootProject.properties["http4k_version"]}")
    api("org.http4k:http4k-contract-ui-swagger:${rootProject.properties["http4k_version"]}")
    api("de.sven-jacobs:loremipsum:_")
}
