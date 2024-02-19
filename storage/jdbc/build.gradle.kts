dependencies {
    api(Libs.http4k_format_moshi)
    api(Libs.exposed_core)
    api(Libs.exposed_jdbc)

    testImplementation(Libs.hikaricp)
    testImplementation(Libs.h2)
}
