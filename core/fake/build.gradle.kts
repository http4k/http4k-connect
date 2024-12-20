import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
}

dependencies {
    api(project(":http4k-connect-core"))
    api(project(":http4k-connect-storage-core"))
    api("org.http4k:http4k-testing-chaos:${rootProject.properties["http4k_version"]}")
    api("org.http4k:http4k-format-moshi:${rootProject.properties["http4k_version"]}")
}
