package org.http4k.connect.amazon.secretsmanager

import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.model.Timestamp
import org.http4k.connect.amazon.model.VersionId
import org.http4k.connect.amazon.secretsmanager.action.CreateSecret
import org.http4k.connect.amazon.secretsmanager.action.CreatedSecret
import org.http4k.connect.amazon.secretsmanager.action.DeleteSecret
import org.http4k.connect.amazon.secretsmanager.action.DeletedSecret
import org.http4k.connect.amazon.secretsmanager.action.GetSecretValue
import org.http4k.connect.amazon.secretsmanager.action.ListSecrets
import org.http4k.connect.amazon.secretsmanager.action.PutSecretValue
import org.http4k.connect.amazon.secretsmanager.action.Secret
import org.http4k.connect.amazon.secretsmanager.action.SecretValue
import org.http4k.connect.amazon.secretsmanager.action.Secrets
import org.http4k.connect.amazon.secretsmanager.action.UpdateSecret
import org.http4k.connect.amazon.secretsmanager.action.UpdatedSecret
import org.http4k.connect.amazon.secretsmanager.action.UpdatedSecretValue
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock
import java.util.UUID.randomUUID

data class StoredSecretValue(
    val versionId: VersionId,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
    val secretString: String? = null,
    val secretBinary: Base64Blob? = null
)

class FakeSecretsManager(
    private val secrets: Storage<StoredSecretValue> = Storage.InMemory(),
    private val clock: Clock = Clock.systemDefaultZone()
) : ChaosFake() {

    private val api = AmazonJsonFake(SecretsManagerMoshi, AwsService.of("secretsmanager"))

    override val app = routes(
        "/" bind POST to routes(
            createSecret(),
            deleteSecret(),
            getSecret(),
            listSecrets(),
            putSecret(),
            updateSecret()
        )
    )

    private fun createSecret() = api.route<CreateSecret> { req ->
        val versionId = VersionId.of(randomUUID().toString())
        val createdAt = Timestamp.of(clock.instant().toEpochMilli() / 1000)
        secrets[req.Name] = StoredSecretValue(
            versionId,
            createdAt, createdAt,
            req.SecretString, req.SecretBinary
        )
        CreatedSecret(SecretId.of(req.Name).toArn(), req.Name, versionId)
    }

    private fun deleteSecret() = api.route<DeleteSecret> { req ->
        val secretId = req.SecretId.resourceId()

        secrets[secretId.value]
            ?.let {
                secrets.remove(secretId.value)
                DeletedSecret(secretId.value, secretId.toArn(), Timestamp.of(0))
            }
    }

    private fun getSecret() = api.route<GetSecretValue> { req ->
        val secretId = req.SecretId.resourceId()

        secrets.keySet(secretId.value).firstOrNull()
            ?.let { secrets[it] }
            ?.let {
                SecretValue(
                    secretId.toArn(),
                    Timestamp.of(0),
                    secretId.value,
                    it.secretBinary,
                    it.secretString,
                    it.versionId,
                    emptyList()
                )
            }
    }

    private fun listSecrets() = api.route<ListSecrets> {
        Secrets(secrets.keySet("").map {
            Secret(SecretId.of(it).toArn(), it)
        })
    }

    private fun putSecret() = api.route<PutSecretValue> { req ->
        val secretId = req.SecretId.resourceId()
        secrets[secretId.value]
            ?.let {
                val versionId = VersionId.of(randomUUID().toString())
                secrets[secretId.value] = StoredSecretValue(
                    versionId,
                    it.createdAt,
                    Timestamp.of(clock.instant().toEpochMilli() / 1000),
                    req.SecretString, req.SecretBinary
                )

                UpdatedSecretValue(secretId.toArn(), secretId.value, versionId)
            }
    }

    private fun updateSecret() = api.route<UpdateSecret> { req ->
        val secretId = req.SecretId.resourceId()

        secrets[secretId.value]
            ?.let {
                val versionId = VersionId.of(randomUUID().toString())
                secrets[secretId.value] = StoredSecretValue(
                    versionId,
                    it.createdAt,
                    Timestamp.of(clock.instant().toEpochMilli() / 1000),
                    req.SecretString, req.SecretBinary
                )

                UpdatedSecret(secretId.toArn(), secretId.value, versionId)
            }
    }

    private fun SecretId.toArn() = ARN.of(
        SecretsManager.awsService,
        Region.of("us-east-1"),
        AwsAccount.of("0"),
        "secret", this
    )

    /**
     * Convenience function to get SecretsManager client
     */
    fun client() = SecretsManager.Http(
        Region.of("ldn-north-1"),
        { AwsCredentials("accessKey", "secret") }, this, clock
    )
}

private fun SecretId.resourceId() = SecretId.of(
    when {
        value.startsWith("arn") -> value.split(":").last()
        else -> value
    }
)

fun main() {
    FakeSecretsManager().start()
}
