import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
}

dependencies {
    api(project(":http4k-connect-core"))
    api(project(":http4k-connect-storage-core"))
    api("org.http4k:http4k-testing-chaos")
    implementation("org.http4k:http4k-format-moshi")
}
