dependencies {
    implementation(Libs.http4k_format_moshi)
    implementation(Libs.exposed_core)
    implementation(Libs.exposed_jdbc)

    testImplementation(Libs.hikaricp)
    testImplementation(Libs.h2)
}
