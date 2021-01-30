dependencies {
    api("dev.forkhandles:values4k")
    compileOnly("org.http4k:http4k-format-moshi")
    testCompileOnly("org.http4k:http4k-format-moshi")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xallow-result-return-type"
    }
}
