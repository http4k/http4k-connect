import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    implementation(libs.parser4k)
    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
    testImplementation(project(path = ":http4k-connect-amazon-s3"))
}
