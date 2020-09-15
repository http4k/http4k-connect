plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation(project(":example:http4k-connect-example"))
}

tasks {
    test {
        useJUnitPlatform()
    }
}
