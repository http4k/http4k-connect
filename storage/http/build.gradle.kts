import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
    id("org.http4k.connect.storage")
}

dependencies {
    api("org.http4k:http4k-contract:${rootProject.properties["http4k_version"]}")
    api("org.http4k:http4k-format-jackson:${rootProject.properties["http4k_version"]}")
    api("org.webjars:swagger-ui:_")
}
