import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(Libs.http4k_contract)
    api(Libs.http4k_format_jackson)
    api(Libs.swagger_ui)
}
