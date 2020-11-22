package org.http4k.connect.amazon.secretsmanager

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.http4k.connect.Choice2
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

    private val sm by lazy {
        SecretsManager.Http(aws.scope, { aws.credentials },
            PrintRequestAndResponse().then(http))
    }

    private val name = UUID.randomUUID().toString()
    private val secretValue = UUID.randomUUID().toString()
    private val updatedValue = UUID.randomUUID().toString()

    open fun setUp() {}

    @BeforeEach
    fun cleanup() {
        setUp()
        with(sm) {
            list(ListSecrets.Request()).successValue().SecretList
                .forEach {
                    delete(DeleteSecret.Request(SecretId(it.ARN!!), true)).successValue()
                }
        }
    }

    @Test
    fun `secret lifecycle`() {
        with(sm) {
            val lookupNothing = lookup(GetSecret.Request(SecretId(name))).successValue()
            assertThat(lookupNothing, absent())

            val creation = create(CreateSecret.Request(name, UUID.randomUUID(), Choice2._2(secretValue))).successValue()
            assertThat(creation.Name, equalTo(name))

            val list = list(ListSecrets.Request()).successValue()
            assertThat(list.SecretList.any { it.ARN == creation.ARN }, equalTo(true))

            val lookupCreated = lookup(GetSecret.Request(SecretId(name))).successValue()
            assertThat(lookupCreated!!.SecretString, present(equalTo(secretValue)))

            val updated = update(UpdateSecret.Request(SecretId(name), UUID.randomUUID(), Choice2._2(updatedValue))).successValue()
            assertThat(updated!!.Name, present(equalTo(name)))

            val lookupUpdated = lookup(GetSecret.Request(SecretId(name))).successValue()
            assertThat(lookupUpdated?.SecretString, present(equalTo(updatedValue)))

            val deleted = delete(DeleteSecret.Request(SecretId(name))).successValue()
            assertThat(deleted?.ARN, present(equalTo(updated.ARN)))

            val lookupDeleted = lookup(GetSecret.Request(SecretId(name))).successValue()
            assertThat(lookupDeleted, absent())
        }
    }
}
