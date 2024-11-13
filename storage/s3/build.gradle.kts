import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(project(":http4k-connect-amazon-s3"))
    implementation(Libs.http4k_format_moshi)
    testImplementation(project(path = ":http4k-connect-amazon-s3", configuration = "testArtifacts"))
    testImplementation(project(":http4k-connect-amazon-s3-fake"))
}
