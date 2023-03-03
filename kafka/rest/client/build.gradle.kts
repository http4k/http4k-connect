import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.davidmc24.gradle.plugin.avro")
}

dependencies {
    api(Libs.http4k_format_moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    implementation("org.apache.avro:avro:_")
    implementation(Libs.api)

    testApi(libs.kotlin.reflect)

    testApi(project(":http4k-connect-kafka-rest-fake"))
    testFixturesApi(Libs.api)
}

tasks {
    withType<KotlinCompile>().configureEach {
        dependsOn("generateTestAvroJava")
    }
}
