import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
}

dependencies {
    api(Libs.http4k_format_moshi)
    api("org.jetbrains.exposed:exposed-core:_")
    api("org.jetbrains.exposed:exposed-jdbc:_")

    testFixturesApi("com.zaxxer:HikariCP:_")
    testFixturesApi("com.h2database:h2:_")
}
