val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    implementation(Libs.http4k_format_moshi)

    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
