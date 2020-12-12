package org.http4k.connect.amazon.secretsmanager

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import dev.forkhandles.result4k.failureOrNull
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class SecretsManagerContract(http: HttpHandler) : AwsContract(AwsService.of("secretsmanager"), http) {

    private val sm by lazy {
        SecretsManager.Http(aws.scope, { aws.credentials }, http)
    }

    private val name = UUID.randomUUID().toString()
    private val secretValue = UUID.randomUUID().toString()
    private val updatedValue = UUID.randomUUID().toString()
    private val finalValue = UUID.randomUUID().toString()

    @Test
    fun `secret lifecycle`() {
        try {
            val lookupNothing = sm(GetSecretValue(SecretId.of(name))).failureOrNull()
            assertThat(lookupNothing?.status, equalTo(BAD_REQUEST))

            val creation = sm(CreateSecret(name, UUID.randomUUID(), secretValue)).successValue()
            assertThat(creation.Name, equalTo(name))

            val list = sm(ListSecrets()).successValue()
            assertThat(list.SecretList.any { it.ARN == creation.ARN }, equalTo(true))

            val lookupCreated = sm(GetSecretValue(SecretId.of(name))).successValue()
            assertThat(lookupCreated.SecretString, present(equalTo(secretValue)))

            val updated = sm(UpdateSecret(SecretId.of(name), UUID.randomUUID(), updatedValue)).successValue()
            assertThat(updated.Name, present(equalTo(name)))

            val putValue = sm(PutSecretValue(SecretId.of(name), UUID.randomUUID(), finalValue)).successValue()
            assertThat(putValue.Name, present(equalTo(name)))

            val lookupUpdated = sm(GetSecretValue(SecretId.of(name))).successValue()
            assertThat(lookupUpdated.SecretString, present(equalTo(finalValue)))

            val deleted = sm(DeleteSecret(SecretId.of(name))).successValue()
            assertThat(deleted.ARN, present(equalTo(updated.ARN)))

            val lookupDeleted = sm(GetSecretValue(SecretId.of(name))).failureOrNull()
            assertThat(lookupDeleted?.status, equalTo(BAD_REQUEST))
        } finally {
            sm(DeleteSecret(SecretId.of(name)))
        }
    }
}
