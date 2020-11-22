package org.http4k.connect.amazon.secretsmanager

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.Timestamp
import org.http4k.connect.amazon.model.VersionId
import org.http4k.connect.amazon.secretsmanager.SecretsManagerJackson.auto
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.INTERNAL_SERVER_ERROR
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.routing.bind
import org.http4k.routing.header
import org.http4k.routing.routes
import java.time.Clock
import java.util.UUID

data class SecretValue(
    val versionId: VersionId,
    val secretString: String? = null,
    val secretBinary: Base64Blob? = null)

class FakeSecretsManager(
    private val secrets: Storage<SecretValue> = Storage.InMemory(),
    private val clock: Clock = Clock.systemDefaultZone()
) : ChaosFake() {

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

    private fun createSecret() = header("X-Amz-Target", "secretsmanager.CreateSecret") bind {
        val req = Body.auto<CreateSecret.Request>().toLens()(it)

        secrets[req.Name] = SecretValue(VersionId(UUID.randomUUID().toString()), req.SecretString, req.SecretBinary)

        Response(OK)
            .with(Body.auto<CreateSecret.Response>().toLens()
                of CreateSecret.Response(req.Name.toArn(), req.Name))
    }

    private fun deleteSecret() = header("X-Amz-Target", "secretsmanager.DeleteSecret") bind {
        Response(INTERNAL_SERVER_ERROR)
    }

    private fun getSecret() = header("X-Amz-Target", "secretsmanager.GetSecretValue") bind {
        val req = Body.auto<GetSecret.Request>().toLens()(it)
        val resourceId = when {
            req.SecretId.value.startsWith("arn") -> req.SecretId.value.split(":").last()
            else -> req.SecretId.value
        }

        secrets.keySet(resourceId).firstOrNull()
            ?.let { secrets[it] }
            ?.let {
                Response(OK)
                    .with(Body.auto<Any>().toLens() of GetSecret.Response(
                        resourceId.toArn(),
                        Timestamp(0),
                        resourceId,
                        it.secretBinary,
                        it.secretString,
                        it.versionId,
                        emptyList()
                    ))
            }
            ?: Response(BAD_REQUEST)
                .with(Body.auto<SecretsManagerError>().toLens()
                    of SecretsManagerError("ResourceNotFoundException", "Secrets Manager can't find the specified secret."))
    }

    private fun listSecrets() = header("X-Amz-Target", "secretsmanager.ListSecrets") bind {
        Response(OK)
            .with(Body.auto<ListSecrets.Response>().toLens()
                of ListSecrets.Response(secrets.keySet("").map {
                ListSecrets.Secret(it.toArn(), it)
            }))
    }

    private fun putSecret() = header("X-Amz-Target", "secretsmanager.PutSecretValue") bind {
        Response(INTERNAL_SERVER_ERROR)
    }

    private fun updateSecret() = header("X-Amz-Target", "secretsmanager.UpdateSecret") bind {
        Response(INTERNAL_SERVER_ERROR)
    }

    private fun String.toArn() = ARN(
        Region("us-east-1"),
        AwsService("secretsmanager"),
        "secret", this,
        AwsAccount(0))

    private fun keyFor(name: String, arn: ARN) = "$name^$arn"

    /**
     * Convenience function to get SecretsManager client
     */
    fun client() = SecretsManager.Http(
        AwsCredentialScope("*", "s3"),
        { AwsCredentials("accessKey", "secret") }, this, clock)
}

data class SecretsManagerError(val __type: String, val Message: String)

fun main() {
    FakeSecretsManager().start()
}
