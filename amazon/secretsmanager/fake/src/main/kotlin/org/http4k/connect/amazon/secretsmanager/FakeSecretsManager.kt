package org.http4k.connect.amazon.secretsmanager

import org.http4k.aws.AwsCredentialScope
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
import org.http4k.connect.amazon.secretsmanager.SecretsManagerJackson.auto
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.routing.bind
import org.http4k.routing.header
import org.http4k.routing.routes
import java.time.Clock
import java.util.UUID.randomUUID

data class SecretValue(
    val versionId: VersionId,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
    val secretString: String? = null,
    val secretBinary: Base64Blob? = null)

class FakeSecretsManager(
    private val secrets: Storage<SecretValue> = Storage.InMemory(),
    private val clock: Clock = Clock.systemDefaultZone()
) : ChaosFake() {

    private val api = AmazonJsonFake(SecretsManagerJackson, AwsService.of("secretsmanager"))

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

    private fun createSecret() = api.route<CreateSecret, CreateSecret.Request> { req ->
        val versionId = VersionId.of(randomUUID().toString())
        val createdAt = Timestamp.of(clock.instant().toEpochMilli() / 1000)
        secrets[req.Name] = SecretValue(versionId,
            createdAt, createdAt,
            req.SecretString, req.SecretBinary)
        CreateSecret.Response(req.Name.toArn(), req.Name, versionId)
    }

    private fun deleteSecret() = api.route<DeleteSecret, DeleteSecret.Request> { req ->
        val resourceId = req.SecretId.resourceId()

        secrets[resourceId]
            ?.let {
                secrets.remove(resourceId)
                DeleteSecret.Response(resourceId, resourceId.toArn(), Timestamp.of(0))
            }
    }

    private fun getSecret() = header("X-Amz-Target", "secretsmanager.GetSecretValue") bind {
        val req = Body.auto<GetSecret.Request>().toLens()(it)
        val resourceId = req.SecretId.resourceId()

        secrets.keySet(resourceId).firstOrNull()
            ?.let { secrets[it] }
            ?.let {
                Response(OK)
                    .with(Body.auto<Any>().toLens() of GetSecret.Response(
                        resourceId.toArn(),
                        Timestamp.of(0),
                        resourceId,
                        it.secretBinary,
                        it.secretString,
                        it.versionId,
                        emptyList()
                    ))
            }
            ?: NOT_FOUND
    }

    private fun listSecrets() = header("X-Amz-Target", "secretsmanager.ListSecrets") bind {
        Response(OK)
            .with(Body.auto<ListSecrets.Response>().toLens()
                of ListSecrets.Response(secrets.keySet("").map {
                ListSecrets.Secret(it.toArn(), it)
            }))
    }

    private fun putSecret() = header("X-Amz-Target", "secretsmanager.PutSecretValue") bind {
        val req = Body.auto<PutSecret.Request>().toLens()(it)
        val resourceId = req.SecretId.resourceId()

        secrets[resourceId]
            ?.let {
                val versionId = VersionId.of(randomUUID().toString())
                secrets[resourceId] = SecretValue(versionId,
                    it.createdAt,
                    Timestamp.of(clock.instant().toEpochMilli() / 1000),
                    req.SecretString, req.SecretBinary)

                Response(OK)
                    .with(Body.auto<PutSecret.Response>().toLens()
                        of PutSecret.Response(resourceId.toArn(), resourceId, versionId))
            }
            ?: NOT_FOUND
    }

    private fun updateSecret() = header("X-Amz-Target", "secretsmanager.UpdateSecret") bind {
        val req = Body.auto<UpdateSecret.Request>().toLens()(it)
        val resourceId = req.SecretId.resourceId()

        secrets[resourceId]
            ?.let {
                val versionId = VersionId.of(randomUUID().toString())
                secrets[resourceId] = SecretValue(versionId,
                    it.createdAt,
                    Timestamp.of(clock.instant().toEpochMilli() / 1000),
                    req.SecretString, req.SecretBinary)

                Response(OK)
                    .with(Body.auto<UpdateSecret.Response>().toLens()
                        of UpdateSecret.Response(resourceId.toArn(), resourceId, versionId))
            }
            ?: NOT_FOUND
    }

    private fun String.toArn() = ARN.of(
        Region.of("us-east-1"),
        AwsService.of("secretsmanager"),
        "secret", this,
        AwsAccount.of("0"))

    /**
     * Convenience function to get SecretsManager client
     */
    fun client() = SecretsManager.Http(
        AwsCredentialScope("*", "s3"),
        { AwsCredentials("accessKey", "secret") }, this, clock)
}

private val NOT_FOUND = Response(BAD_REQUEST)
    .with(Body.auto<SecretsManagerError>().toLens()
        of SecretsManagerError("ResourceNotFoundException", "Secrets Manager can't find the specified secret."))

private fun SecretId.resourceId() = when {
    value.startsWith("arn") -> value.split(":").last()
    else -> value
}

data class SecretsManagerError(val __type: String, val Message: String)

fun main() {
    FakeSecretsManager().start()
}
