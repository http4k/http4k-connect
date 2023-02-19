dependencies {
    api(Libs.http4k_format_moshi) {
        exclude("org.jetbrains.kotlin", "kotlin-reflect")
    }

    implementation(Libs.api)

    testApi("org.jetbrains.kotlin:kotlin-reflect:_")

    testFixturesApi(Libs.api)
}
