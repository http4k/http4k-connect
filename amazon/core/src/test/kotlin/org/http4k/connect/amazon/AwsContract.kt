package org.http4k.connect.amazon

import org.http4k.connect.amazon.model.AwsService
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.BeforeEach

abstract class AwsContract(val service: AwsService, protected val http: HttpHandler) {
    abstract val aws: AwsEnvironment

    open fun setUp() {}

    @BeforeEach
    fun cleanup() {
        setUp()
    }
}
