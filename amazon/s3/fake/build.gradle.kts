import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    implementation(Libs.http4k_template_pebble)
    testFixturesApi("software.amazon.awssdk:s3:_")
    testFixturesApi(testFixtures(project(":http4k-connect-amazon-core")))
}
