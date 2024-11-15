import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
    id("org.http4k.connect.client")
}

dependencies {
    api(project(":http4k-connect-amazon-core"))
    api("org.http4k:http4k-format-moshi:${rootProject.properties["http4k_version"]}") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    api("org.http4k:http4k-security-oauth:${rootProject.properties["http4k_version"]}") {
        exclude("org.http4k", "http4k-format-moshi")
    }

    api("se.ansman.kotshi:api:_")
    testFixturesApi("org.bitbucket.b_c:jose4j:_")

    testFixturesApi(testFixtures(project(":http4k-connect-core")))
    testFixturesApi(testFixtures(project(":http4k-connect-amazon-core")))
}
