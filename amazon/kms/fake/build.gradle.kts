dependencies {
    implementation(Libs.http4k_format_moshi)
    implementation("org.bouncycastle:bcprov-jdk18on:_")

    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
