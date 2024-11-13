import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    testFixturesApi(project(path = ":http4k-connect-amazon-sns-fake"))
    testFixturesApi(testFixtures(project(":http4k-connect-amazon-core")))
}
