import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(project(":http4k-connect-core"))

    implementation("org.http4k:http4k-format-moshi")
    implementation("com.squareup:kotlinpoet:_")
    implementation("com.squareup:kotlinpoet-metadata:_")
    implementation("com.squareup:kotlinpoet-ksp:_")
    implementation(libs.symbol.processing.api)

    ksp("se.ansman.kotshi:compiler:_")

    testFixturesApi("se.ansman.kotshi:api:_")
    testFixturesApi("org.http4k:http4k-format-moshi")
    testFixturesApi(libs.result4k)

    kspTest(project(":http4k-connect-ksp-generator"))
    kspTestFixtures(project(":http4k-connect-ksp-generator"))
}

