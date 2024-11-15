import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("com.github.davidmc24.gradle.plugin.avro")
    id("org.http4k.module")
    id("org.http4k.connect.module")
    id("org.http4k.connect.client")
}

dependencies {
    api("org.http4k:http4k-format-moshi:${rootProject.properties["http4k_version"]}") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    api("org.apache.avro:avro:_")
    api("se.ansman.kotshi:api:_")

    testFixturesApi("org.jetbrains.kotlin:kotlin-reflect")

    testFixturesApi(project(":http4k-connect-kafka-rest-fake"))
    testFixturesImplementation("org.apache.avro:avro:_")
    testFixturesApi("se.ansman.kotshi:api:_")
}

tasks {
    withType<KotlinCompile>().configureEach {
        dependsOn("generateTestFixturesAvroJava")
    }
}
