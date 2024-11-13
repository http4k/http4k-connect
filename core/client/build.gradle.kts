import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(Libs.values4k)
    compileOnly(Libs.http4k_format_moshi)
    testCompileOnly(Libs.http4k_format_moshi)
}
