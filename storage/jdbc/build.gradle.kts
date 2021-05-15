dependencies {
    implementation("org.http4k:http4k-format-moshi")
    implementation(Libs.exposed_core)
    implementation(Libs.exposed_jdbc)

    testImplementation(Libs.hikaricp)
    testImplementation(Libs.h2)
}
