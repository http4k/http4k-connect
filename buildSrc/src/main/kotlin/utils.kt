import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.kotlin

fun Project.kotlinProject() {

    dependencies {
        "implementation"(kotlin("stdlib"))
        "implementation"(platform("org.http4k:http4k-bom:${properties["http4k_version"]!!}"))
        "implementation"(platform("dev.forkhandles:forkhandles-bom:${properties["forkhandles_version"]!!}"))
        "implementation"("org.http4k:http4k-core")
        "implementation"("dev.forkhandles:result4k")

        "testImplementation"(platform("org.junit:junit-bom:${properties["junit_version"]!!}"))
        "testImplementation"("org.http4k:http4k-testing-hamkrest")
        "testImplementation"("org.junit.jupiter:junit-jupiter-api")
        "testImplementation"("org.junit.jupiter:junit-jupiter-engine")
    }

    (tasks["test"] as Test).apply {
        useJUnitPlatform()
    }
}

fun Project.fakeProject() {
    dependencies {
        "api"(project(":common"))
    }
}
