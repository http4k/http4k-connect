package org.http4k.connect.amazon.secretsmanager

import org.http4k.connect.amazon.AwsEnvironment
import org.http4k.connect.amazon.systemsmanager.Http
import org.http4k.connect.amazon.systemsmanager.SystemsManager
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.BeforeEach

abstract class SystemsManagerContract(private val http: HttpHandler) {
    abstract val aws: AwsEnvironment

    private val sm by lazy {
        SystemsManager.Http(aws.scope, { aws.credentials }, http)
    }

    open fun setUp() {}

    @BeforeEach
    fun cleanup() {
        setUp()
    }
}
