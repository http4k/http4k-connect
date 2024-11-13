val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    testFixturesApi(Libs.api)

    testImplementation(project(":http4k-connect-kafka-rest"))
}
