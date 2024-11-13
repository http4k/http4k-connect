import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(project(":http4k-connect-ai-core"))

    implementation(Libs.api)

    testApi(Libs.http4k_cloudnative)
    testApi(Libs.http4k_format_moshi)
    testFixturesApi(testFixtures(project(":http4k-connect-core")))
}
