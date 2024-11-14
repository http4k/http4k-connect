import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
    id("com.google.devtools.ksp")
}

dependencies {
    api(project(":http4k-connect-core"))

    implementation("org.http4k:http4k-format-moshi:${rootProject.properties["http4k_version"]}")
    implementation("com.squareup:kotlinpoet:_")
    implementation("com.squareup:kotlinpoet-metadata:_")
    implementation("com.squareup:kotlinpoet-ksp:_")
    implementation("com.google.devtools.ksp:symbol-processing-api:_")

    ksp("se.ansman.kotshi:compiler:_")

    testFixturesApi("se.ansman.kotshi:api:_")
    testFixturesApi("org.http4k:http4k-format-moshi:${rootProject.properties["http4k_version"]}")
    testFixturesApi("dev.forkhandles:result4k")

    kspTest(project(":http4k-connect-ksp-generator"))
    kspTestFixtures(project(":http4k-connect-ksp-generator"))
}

