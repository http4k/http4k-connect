import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(Http4k.securityOauth)
    implementation(libs.jose4j)

    testFixturesApi(testFixtures(project(":http4k-connect-amazon-core")))
}
