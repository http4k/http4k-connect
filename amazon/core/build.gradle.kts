dependencies {
    api("org.http4k:http4k-aws")

    compileOnly("org.http4k:http4k-format-moshi") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    implementation(Libs.api)
    kapt(Libs.se_ansman_kotshi_compiler)

    implementation("org.http4k:http4k-format-core")
}
