import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("com.github.davidmc24.gradle.plugin.avro")
}

dependencies {
    api(Libs.http4k_format_moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    implementation("org.apache.avro:avro:_")

    implementation(Libs.api)

    testFixturesApi(libs.kotlin.reflect)

    testFixturesImplementation("org.apache.avro:avro:_")

    testFixturesApi(Libs.api)
}

tasks {
    withType<KotlinCompile>().configureEach {
        dependsOn("generateTestFixturesAvroJava")
    }
}
