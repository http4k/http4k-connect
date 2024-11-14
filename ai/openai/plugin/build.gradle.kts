import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
    id("org.http4k.connect.client")
}

dependencies {
    api("org.http4k:http4k-contract:${rootProject.properties["http4k_version"]}")
    api("org.http4k:http4k-format-jackson:${rootProject.properties["http4k_version"]}")
    api("org.http4k:http4k-security-oauth:${rootProject.properties["http4k_version"]}")
    api(project(":http4k-connect-storage-core"))

    compileOnly(platform("org.junit:junit-bom:_"))
    compileOnly("org.junit.jupiter:junit-jupiter-api")
    compileOnly("org.http4k:http4k-testing-approval:${rootProject.properties["http4k_version"]}")
    compileOnly("org.http4k:http4k-testing-hamkrest:${rootProject.properties["http4k_version"]}")

    testApi(project(":http4k-connect-ai-openai-fake"))
    testApi("org.junit.jupiter:junit-jupiter-api")
    testApi("org.http4k:http4k-serverless-lambda:${rootProject.properties["http4k_version"]}")
    testApi("org.http4k:http4k-cloudnative:${rootProject.properties["http4k_version"]}")
    testApi("org.http4k:http4k-testing-approval:${rootProject.properties["http4k_version"]}")

    testApi("com.nimbusds:nimbus-jose-jwt:_")
}
