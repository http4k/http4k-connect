dependencies {
    api(Libs.http4k_contract)
    api(Libs.http4k_format_jackson)
    api(Libs.http4k_security_oauth)
    api(project(":http4k-connect-storage-core"))

    compileOnly(platform(Libs.junit_bom))
    compileOnly(Libs.junit_jupiter_api)
    compileOnly(Libs.http4k_testing_approval)
    compileOnly(Libs.http4k_testing_hamkrest)

    testApi(project(":http4k-connect-openai-fake"))
    testApi(Libs.junit_jupiter_api)
    testApi(Libs.http4k_serverless_lambda)
    testApi(Libs.http4k_cloudnative)
    testApi(Libs.http4k_testing_approval)

    testApi("com.nimbusds:nimbus-jose-jwt:_")
}
