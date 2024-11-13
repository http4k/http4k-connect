import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(project(":http4k-connect-google-analytics-core"))
    api("org.http4k:http4k-format-moshi") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    implementation(Libs.api)

    implementation("org.http4k:http4k-format-core")
}
