import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(project(":http4k-connect-amazon-core"))
    api(Libs.http4k_format_moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }
    compileOnly(Libs.http4k_serverless_lambda)
    compileOnly("joda-time:joda-time:_")

    implementation(Libs.api)

    testImplementation(project(path = ":http4k-connect-core", configuration = "testArtifacts"))
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
