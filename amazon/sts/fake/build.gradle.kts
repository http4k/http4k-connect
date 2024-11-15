import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
    id("org.http4k.connect.fake")
}

dependencies {
    api("org.http4k:http4k-template-pebble:${rootProject.properties["http4k_version"]}")

    testFixturesApi(testFixtures(project(":http4k-connect-amazon-core")))
    testFixturesApi(project(":http4k-connect-amazon-sqs"))
}
