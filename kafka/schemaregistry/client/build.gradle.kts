import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.davidmc24.gradle.plugin.avro")
}

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(Libs.http4k_format_moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    implementation("org.apache.avro:avro:_")

    implementation(Libs.api)

    testApi(libs.kotlin.reflect)

    testFixturesApi(Libs.api)
}

tasks {
    withType<KotlinCompile>().configureEach {
        dependsOn("generateTestAvroJava")
    }
}
