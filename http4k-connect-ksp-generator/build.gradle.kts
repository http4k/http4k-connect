import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    implementation(Libs.http4k_format_moshi)
    implementation(Libs.kotlinpoet)
    implementation(Libs.kotlinpoet_metadata)
    implementation(Libs.kotlinpoet_ksp)
    implementation(libs.symbol.processing.api)

    testFixturesApi(Libs.api)
    testFixturesApi(Libs.http4k_format_moshi)
    testFixturesApi(libs.result4k)

    kspTestFixtures(project(":http4k-connect-ksp-generator"))
}

