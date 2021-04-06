dependencies {
    api("org.http4k:http4k-aws")

    compileOnly("org.http4k:http4k-format-moshi") {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    implementation("se.ansman.kotshi:api:2.3.3")
    kapt("se.ansman.kotshi:compiler:2.3.3")

    implementation("org.http4k:http4k-format-core")
}
