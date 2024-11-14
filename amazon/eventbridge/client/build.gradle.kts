import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
    id("org.http4k.connect.client")
}

dependencies {
    api(project(":http4k-connect-amazon-core"))
    api("org.http4k:http4k-format-moshi:${rootProject.properties["http4k_version"]}") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    compileOnly("org.http4k:http4k-serverless-lambda:${rootProject.properties["http4k_version"]}")
    compileOnly("joda-time:joda-time:_")

    implementation("se.ansman.kotshi:api:_")

    testFixturesApi(testFixtures(project(":http4k-connect-core")))
    testFixturesApi(testFixtures(project(":http4k-connect-amazon-core")))
}
