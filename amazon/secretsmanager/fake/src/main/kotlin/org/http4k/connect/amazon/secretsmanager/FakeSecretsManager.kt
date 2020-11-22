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
        "/" bind routes(
            POST to routes(
                createSecret(),
                deleteSecret(),
                getSecret(),
                listSecrets(),
                putSecret(),
                updateSecret()
            )
        )
    )

    private fun createSecret() = header("X-Amz-Target", "secretsmanager.CreateSecret") bind {
        val req = Body.auto<CreateSecret.Request>().toLens()(it)

        val arn = randomARN(req.Name)

        secrets[arn.toString()] = SecretValue(req.SecretString, req.SecretBinary)

        Response(OK)
            .with(Body.auto<CreateSecret.Response>().toLens()
                of CreateSecret.Response(arn, req.Name))
    }

    private fun deleteSecret() = header("X-Amz-Target", "secretsmanager.DeleteSecret") bind {
        Response(INTERNAL_SERVER_ERROR)
    }

    private fun getSecret() = header("X-Amz-Target", "secretsmanager.GetSecretValue") bind {
        Response(INTERNAL_SERVER_ERROR)
    }

    private fun listSecrets() = header("X-Amz-Target", "secretsmanager.ListSecrets") bind {
        Response(INTERNAL_SERVER_ERROR)
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

    /**
     * Convenience function to get SecretsManager client
     */
    fun client() = SecretsManager.Http(
        AwsCredentialScope("*", "s3"),
        { AwsCredentials("accessKey", "secret") }, this, clock)

}

fun main() {
    FakeSecretsManager().start()
}
