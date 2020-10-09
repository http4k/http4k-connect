dependencies {
    implementation("org.http4k:http4k-format-jackson")
    implementation("org.jetbrains.exposed:exposed-core:0.27.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.27.1")
//    implementation("com.zaxxer:HikariCP:2.7.8")
    implementation("org.postgresql:postgresql:42.2.16")

    testImplementation("org.testcontainers:postgresql")
}
