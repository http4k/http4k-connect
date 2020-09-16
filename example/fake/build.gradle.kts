plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":http4k-connect-example"))
    testImplementation("org.http4k:http4k-testing-chaos")
}
