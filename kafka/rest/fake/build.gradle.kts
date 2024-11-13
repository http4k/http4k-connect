import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    testFixturesApi("se.ansman.kotshi:api:_")

    testFixturesApi(project(":http4k-connect-kafka-rest"))
}
