import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("com.github.davidmc24.gradle.plugin.avro")
    id("org.http4k.module")
}

dependencies {
    api("org.http4k:http4k-format-moshi") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    implementation("org.apache.avro:avro:_")
    implementation(Libs.api)

    testFixturesApi(libs.kotlin.reflect)

    testFixturesApi(project(":http4k-connect-kafka-rest-fake"))
    testFixturesImplementation("org.apache.avro:avro:_")
    testFixturesApi(Libs.api)
}

tasks {
    withType<KotlinCompile>().configureEach {
        dependsOn("generateTestFixturesAvroJava")
    }
}
