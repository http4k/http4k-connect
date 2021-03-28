dependencies {
    implementation("org.http4k:http4k-format-moshi")
    implementation("org.jetbrains.exposed:exposed-core:0.29.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.29.1")

    testImplementation("com.zaxxer:HikariCP:2.7.8")
    testImplementation("com.h2database:h2:1.4.190")
}
