import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(Libs.http4k_aws)

    api(Libs.http4k_format_moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    implementation(Libs.api)
    implementation(Libs.http4k_format_core)

    testImplementation(Libs.http4k_testing_chaos)
}
