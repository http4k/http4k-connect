import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api("org.http4k:http4k-contract")
    api("org.http4k:http4k-format-jackson")
    api("org.webjars:swagger-ui:_")
}
