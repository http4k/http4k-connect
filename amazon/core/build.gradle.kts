import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api("org.http4k:http4k-aws")

    api(Libs.http4k_format_moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    implementation(Libs.api)
    implementation("org.http4k:http4k-format-core")

    testFixturesApi("org.http4k:http4k-testing-chaos")
}
