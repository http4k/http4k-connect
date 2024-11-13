import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api("org.http4k:http4k-aws")

    api("org.http4k:http4k-format-moshi") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    implementation("se.ansman.kotshi:api:_")
    implementation("org.http4k:http4k-format-core")

    testFixturesApi("org.http4k:http4k-testing-chaos")
}
