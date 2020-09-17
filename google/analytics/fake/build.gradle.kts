plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":http4k-connect-google-analytics"))
    testImplementation("org.http4k:http4k-testing-chaos")
}
