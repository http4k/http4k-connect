import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
    id("org.http4k.connect.client")
}

dependencies {
    api("org.http4k:http4k-cloudnative")
    api("org.http4k:http4k-format-moshi") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation("se.ansman.kotshi:api:_")

    testApi("org.http4k:http4k-format-moshi")
    testFixturesApi(testFixtures(project(":http4k-connect-core")))
}
