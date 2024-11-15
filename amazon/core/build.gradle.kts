import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
    id("org.http4k.connect.client")
}

dependencies {
    api("org.http4k:http4k-aws:${rootProject.properties["http4k_version"]}")

    api("org.http4k:http4k-format-moshi:${rootProject.properties["http4k_version"]}") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    api("se.ansman.kotshi:api:_")
    api("org.http4k:http4k-format-core:${rootProject.properties["http4k_version"]}")

    testFixturesApi("org.http4k:http4k-testing-chaos:${rootProject.properties["http4k_version"]}")
}
