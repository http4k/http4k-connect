dependencies {
    api(Libs.values4k)
    compileOnly(Libs.http4k_format_moshi)
    testCompileOnly(Libs.http4k_format_moshi)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xallow-result-return-type"
    }
}
