import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api("org.http4k:http4k-cloudnative")
    api(Libs.http4k_format_moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation(Libs.api)

    testApi(Libs.http4k_format_moshi)
    testFixturesApi(testFixtures(project(":http4k-connect-core")))
}
