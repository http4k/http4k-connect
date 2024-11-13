import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    implementation("org.http4k:http4k-format-moshi")
    implementation("dev.forkhandles:values4k")
    implementation(kotlin("script-runtime"))
}
