import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    testFixturesApi("com.amazonaws:aws-lambda-java-events:_")
    testFixturesApi(Libs.http4k_format_moshi)
    testFixturesApi(testFixtures(project(":http4k-connect-amazon-core")))
}
