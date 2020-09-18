plugins {
    kotlin("jvm")
}

dependencies {
    "implementation"(project(":http4k-connect-core"))
    "implementation"("io.lettuce:lettuce-core:5.3.4.RELEASE")
    "compileOnly"("org.http4k:http4k-format-jackson")
}
