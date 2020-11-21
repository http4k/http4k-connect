package org.http4k.connect.amazon.secretsmanager

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import org.http4k.connect.amazon.AwsEnvironment
import org.http4k.connect.amazon.model.SecretId
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class SecretsManagerContract(private val http: HttpHandler) {
    abstract val aws: AwsEnvironment

    private val secretsManager by lazy {
        SecretsManager.Http(aws.scope, { aws.credentials },
            PrintRequestAndResponse().then(http))
    }

    private val id = SecretId(UUID.randomUUID().toString())

    open fun setUp() {}

    @BeforeEach
    fun cleanup() {
        setUp()
    }

    @Test
    fun `secret lifecycle`() {
        assertThat(secretsManager.lookup(GetSecret.Request(id)), equalTo(Success(null)))
    }
}
