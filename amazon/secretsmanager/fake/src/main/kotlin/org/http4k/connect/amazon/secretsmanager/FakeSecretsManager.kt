package org.http4k.connect.amazon.secretsmanager

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.Region
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
import kotlin.random.Random

data class SecretValue(val secretString: String? = null, val secretBinary: Base64Blob? = null)

class FakeSecretsManager(
    private val secrets: Storage<SecretValue> = Storage.InMemory(),
    private val clock: Clock = Clock.systemDefaultZone(),
    private val random: Random = Random(1)
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

        val arn = randomARN(req.Name)

        secrets[keyFor(req.Name, arn)] = SecretValue(req.SecretString, req.SecretBinary)

        Response(OK)
            .with(Body.auto<CreateSecret.Response>().toLens()
                of CreateSecret.Response(arn, req.Name))
    }

    private fun deleteSecret() = header("X-Amz-Target", "secretsmanager.DeleteSecret") bind {
        Response(INTERNAL_SERVER_ERROR)
    }

    private fun getSecret() = header("X-Amz-Target", "secretsmanager.GetSecretValue") bind {
        val req = Body.auto<GetSecret.Request>().toLens()(it)

//        secrets.keySet(req.SecretId.value)
//            .firstOrNull()
//            ?: secrets.keySet(req.SecretId.value)

//        Response(OK)
//            .with(Body.auto<GetSecret.Response>().toLens()
//                of GetSecret.Response())

        Response(BAD_REQUEST).with(Body.auto<Any>().toLens()
            of SecretsManagerError("ResourceNotFoundException", "Secrets Manager can't find the specified secret."))
    }

    private fun listSecrets() = header("X-Amz-Target", "secretsmanager.ListSecrets") bind {
        Response(OK)
            .with(Body.auto<ListSecrets.Response>().toLens()
                of ListSecrets.Response(secrets.keySet("").map {
                ListSecrets.Secret(ARN(it.split("^")[1]), it.split("^")[0])
            }))
    }

    private fun putSecret() = header("X-Amz-Target", "secretsmanager.PutSecretValue") bind {
        Response(INTERNAL_SERVER_ERROR)
    }

    private fun updateSecret() = header("X-Amz-Target", "secretsmanager.UpdateSecret") bind {
        Response(INTERNAL_SERVER_ERROR)
    }

    private fun randomARN(resourceId: String) = ARN(
        Region("us-east-1"),
        AwsService("secretsmanager"),
        "secret", resourceId,
        AwsAccount(random.nextInt().toLong()))

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
