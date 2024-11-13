import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(project(":http4k-connect-amazon-core"))

    testFixturesApi(Libs.mockk)
    testFixturesApi(testFixtures(project(":http4k-connect-amazon-core")))
}
