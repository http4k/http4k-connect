package org.http4k.connect.amazon.secretsmanager

import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.Timestamp
import org.http4k.connect.amazon.model.VersionId
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock

data class StoredSecretValue(
    val versionId: VersionId,
    val createdAt: Timestamp,
    val updatedAt: Timestamp,
    val secretString: String? = null,
    val secretBinary: Base64Blob? = null
)

class FakeSecretsManager(
    secrets: Storage<StoredSecretValue> = Storage.InMemory(),
    private val clock: Clock = Clock.systemUTC()
) : ChaosFake() {

    private val api = AmazonJsonFake(SecretsManagerMoshi, AwsService.of("secretsmanager"))

    override val app = routes(
        "/" bind POST to routes(
            api.createSecret(secrets, clock),
            api.deleteSecret(secrets),
            api.getSecret(secrets),
            api.listSecrets(secrets),
            api.putSecret(secrets, clock),
            api.updateSecret(secrets, clock)
        )
    )

    /**
     * Convenience function to get SecretsManager client
     */
    fun client() = SecretsManager.Http(Region.of("ldn-north-1"), { AwsCredentials("accessKey", "secret") }, this, clock)
}

fun main() {
    FakeSecretsManager().start()
}
