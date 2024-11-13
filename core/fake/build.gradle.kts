val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(project(":http4k-connect-storage-core"))
    api(Libs.http4k_testing_chaos)
    implementation(Libs.http4k_format_moshi)
}
