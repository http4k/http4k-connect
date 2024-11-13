import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(project(":http4k-connect-ai-core"))
    implementation("se.ansman.kotshi:api:_")

    testApi("org.http4k:http4k-cloudnative")
    testApi("org.http4k:http4k-format-moshi")
    testFixturesApi(testFixtures(project(":http4k-connect-core")))
}
