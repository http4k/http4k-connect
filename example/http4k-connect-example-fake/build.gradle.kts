plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":example:http4k-connect-example"))
}

tasks {
    test {
        useJUnitPlatform()
    }
}
