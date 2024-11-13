import org.http4k.internal.ModuleLicense.Apache2

val license by project.extra { Apache2 }

plugins {
    id("org.http4k.module")
    id("org.http4k.connect.module")
}

dependencies {
    api("org.http4k:http4k-contract")
    api("org.http4k:http4k-format-jackson")
    api("org.http4k:http4k-security-oauth")
    api(project(":http4k-connect-storage-core"))

    compileOnly(platform("org.junit:junit-bom:_"))
    compileOnly("org.junit.jupiter:junit-jupiter-api")
    compileOnly("org.http4k:http4k-testing-approval")
    compileOnly("org.http4k:http4k-testing-hamkrest")

    testApi(project(":http4k-connect-ai-openai-fake"))
    testApi("org.junit.jupiter:junit-jupiter-api")
    testApi("org.http4k:http4k-serverless-lambda")
    testApi("org.http4k:http4k-cloudnative")
    testApi("org.http4k:http4k-testing-approval")

    testApi("com.nimbusds:nimbus-jose-jwt:_")
}
