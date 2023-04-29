dependencies {
    implementation(Libs.http4k_format_moshi)
    implementation("org.bouncycastle:bcprov-jdk15on:_")
    implementation("org.bouncycastle:bcpkix-jdk15on:_")

    testImplementation(project(path = ":http4k-connect-amazon-core", configuration = "testArtifacts"))
}
