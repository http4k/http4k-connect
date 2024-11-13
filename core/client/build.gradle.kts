import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(Libs.values4k)
    compileOnly("org.http4k:http4k-format-moshi")
    testCompileOnly("org.http4k:http4k-format-moshi")
}
