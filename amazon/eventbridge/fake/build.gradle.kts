val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    implementation("org.http4k:http4k-format-moshi")
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
