import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
}

dependencies {
    testFixturesApi("com.amazonaws:aws-lambda-java-events:_")
    testFixturesApi("org.http4k:http4k-format-moshi")
    testFixturesApi(testFixtures(project(":http4k-connect-amazon-core")))
}
