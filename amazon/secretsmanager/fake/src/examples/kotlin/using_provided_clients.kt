import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.secretsmanager.FakeSecretsManager
import org.http4k.connect.amazon.secretsmanager.createSecret
import org.http4k.connect.amazon.secretsmanager.getSecretValue
import java.util.UUID

fun main() {
    val fakeSm = FakeSecretsManager()
    val client = fakeSm.client()
    val secretId = SecretId.of("a-secret-id")

    println(client.createSecret("friendly name",
        UUID.randomUUID(),
        Base64Blob.encoded("hello"))
    )

    println(client.getSecretValue(secretId))
}
