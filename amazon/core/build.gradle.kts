dependencies {
    api("org.http4k:http4k-aws")

    compileOnly("org.http4k:http4k-format-moshi") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    compileOnly("se.ansman.kotshi:api:2.3.2")
    kapt("se.ansman.kotshi:compiler:2.3.2")

    implementation("org.http4k:http4k-aws")
    implementation("org.http4k:http4k-format-core")
}
