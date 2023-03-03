dependencies {
    implementation("org.apache.avro:avro:_")

    testFixturesApi(Libs.api)

    testImplementation(project(":http4k-connect-kafka-schemaregistry"))
}
