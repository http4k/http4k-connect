import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
}

dependencies {
    implementation("org.http4k:http4k-format-moshi")
    implementation("org.bouncycastle:bcprov-jdk18on:_")

    testFixturesApi(testFixtures(project(":http4k-connect-amazon-core")))
}
