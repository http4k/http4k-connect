import org.gradle.api.Project
import org.gradle.kotlin.dsl.KotlinBuildScript
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

fun Project.kotlinProject() {

    dependencies {
        "implementation"(kotlin("stdlib"))
        "implementation"(platform("org.http4k:http4k-bom:3.260.0"))
        "implementation"("org.http4k:http4k-core")

        "testImplementation"(platform("org.junit:junit-bom:5.7.0"))
        "testImplementation"("org.http4k:http4k-testing-hamkrest")
        "testImplementation"("org.junit.jupiter:junit-jupiter-api")
        "testImplementation"("org.junit.jupiter:junit-jupiter-engine")
    }
}