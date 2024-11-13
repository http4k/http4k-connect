import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    implementation("org.apache.avro:avro:_")

    testFixturesApi(Libs.api)

    testFixturesApi(project(":http4k-connect-kafka-schemaregistry"))
}
