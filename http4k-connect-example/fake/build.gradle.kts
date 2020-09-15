plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation(project(":http4k-connect-example:client"))
}

tasks {
    test {
        useJUnitPlatform()
    }
}
