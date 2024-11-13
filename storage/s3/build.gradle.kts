import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(project(":http4k-connect-amazon-s3"))
    implementation(Libs.http4k_format_moshi)
    testFixturesApi(testFixtures(project(":http4k-connect-amazon-s3")))
    testImplementation(project(":http4k-connect-amazon-s3-fake"))
}
