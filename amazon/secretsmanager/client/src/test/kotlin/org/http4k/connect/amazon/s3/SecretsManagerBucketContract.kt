package org.http4k.connect.amazon.s3

import org.http4k.connect.amazon.AwsEnvironment
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.BeforeEach

abstract class SecretsManagerBucketContract(private val http: HttpHandler) {
    abstract val aws: AwsEnvironment

    open fun setUp() {}

    @BeforeEach
    fun cleanup() {
        setUp()
    }
}
