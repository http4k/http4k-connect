import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
    id("org.http4k.connect.client")
}

dependencies {
    api(project(":http4k-connect-ai-core"))
    api("se.ansman.kotshi:api:_")

    testApi("org.http4k:http4k-cloudnative:${rootProject.properties["http4k_version"]}")
    testApi("org.http4k:http4k-format-moshi:${rootProject.properties["http4k_version"]}")
    testFixturesApi(testFixtures(project(":http4k-connect-core")))
}
