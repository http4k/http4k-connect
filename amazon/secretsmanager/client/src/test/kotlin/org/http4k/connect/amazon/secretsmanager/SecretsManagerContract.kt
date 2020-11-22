package org.http4k.connect.amazon.secretsmanager

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import org.http4k.connect.amazon.AwsEnvironment
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.successValue
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

    private val name = UUID.randomUUID().toString()

    open fun setUp() {}

    @BeforeEach
    fun cleanup() {
        setUp()
        secretsManager.list(ListSecrets.Request()).successValue().SecretList.forEach {
                secretsManager.delete(DeleteSecret.Request(SecretId(it.ARN!!), true)).successValue()
            }
    }

    @Test
    fun `secret lifecycle`() {
        val lookupNothing = secretsManager.lookup(GetSecret.Request(SecretId(name))).successValue()
        assertThat(lookupNothing, absent())
        val creation = secretsManager.create(CreateSecret.Request(name, UUID.randomUUID())).successValue()
        val lookupCreated = secretsManager.lookup(GetSecret.Request(SecretId(creation.ARN))).successValue()
        println(lookupCreated)
    }
}
