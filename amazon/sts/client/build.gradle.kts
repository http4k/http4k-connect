val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(project(":http4k-connect-amazon-core"))

    testImplementation(Libs.mockk)
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
