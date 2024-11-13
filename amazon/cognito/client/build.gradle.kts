import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
    id("org.http4k.connect.client")
}

dependencies {
    api(project(":http4k-connect-amazon-core"))
    api("org.http4k:http4k-format-moshi") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    api("org.http4k:http4k-security-oauth") {
        exclude("org.http4k", "http4k-format-moshi")
    }

    implementation("se.ansman.kotshi:api:_")
    testFixturesApi(libs.jose4j)

    testFixturesApi(testFixtures(project(":http4k-connect-core")))
    testFixturesApi(testFixtures(project(":http4k-connect-amazon-core")))
}
