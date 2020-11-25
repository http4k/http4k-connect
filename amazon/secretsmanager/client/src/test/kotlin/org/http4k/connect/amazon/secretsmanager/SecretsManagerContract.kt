package org.http4k.connect.amazon.secretsmanager

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import org.http4k.connect.amazon.AwsEnvironment
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class SecretsManagerContract(private val http: HttpHandler) {
    abstract val aws: AwsEnvironment

    private val sm by lazy {
        SecretsManager.Http(aws.scope, { aws.credentials }, http)
    }

    private val name = UUID.randomUUID().toString()
    private val secretValue = UUID.randomUUID().toString()
    private val updatedValue = UUID.randomUUID().toString()
    private val finalValue = UUID.randomUUID().toString()

    open fun setUp() {}

    @BeforeEach
    fun cleanup() {
        setUp()
        with(sm) {
            list(ListSecrets.Request()).successValue().SecretList
                .forEach {
                    delete(DeleteSecret.Request(SecretId.of(it.arn!!), true)).successValue()
                }
        }
    }

    @Test
    fun `secret lifecycle`() {
        with(sm) {
            val lookupNothing = get(GetSecret.Request(SecretId.of(name))).successValue()
            assertThat(lookupNothing, absent())

            val creation = create(CreateSecret.Request(name, UUID.randomUUID(), secretValue)).successValue()
            assertThat(creation.Name, equalTo(name))

            val list = list(ListSecrets.Request()).successValue()
            assertThat(list.SecretList.any { it.arn == creation.arn }, equalTo(true))

            val lookupCreated = get(GetSecret.Request(SecretId.of(name))).successValue()
            assertThat(lookupCreated!!.SecretString, present(equalTo(secretValue)))

            val updated = update(UpdateSecret.Request(SecretId.of(name), UUID.randomUUID(), updatedValue)).successValue()
            assertThat(updated!!.Name, present(equalTo(name)))

            val putValue = put(PutSecret.Request(SecretId.of(name), UUID.randomUUID(), finalValue)).successValue()
            assertThat(putValue!!.Name, present(equalTo(name)))

            val lookupUpdated = get(GetSecret.Request(SecretId.of(name))).successValue()
            assertThat(lookupUpdated?.SecretString, present(equalTo(finalValue)))

            val deleted = delete(DeleteSecret.Request(SecretId.of(name))).successValue()
            assertThat(deleted?.arn, present(equalTo(updated.arn)))

            val lookupDeleted = get(GetSecret.Request(SecretId.of(name))).successValue()
            assertThat(lookupDeleted, absent())
        }
    }
}
