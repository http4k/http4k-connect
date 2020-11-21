import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.secretsmanager.CreateSecret
import org.http4k.connect.amazon.secretsmanager.FakeSecretsManager
import org.http4k.connect.amazon.secretsmanager.GetSecret

fun main() {
    val fakeSm = FakeSecretsManager()
    val client = fakeSm.client()
    val secretId = SecretId("a-secret-id")

    println(client.create(
        CreateSecret.Request("friendly name",
            Base64Blob.encoded("hello")))
    )

    println(client.lookup(GetSecret.Request(secretId)))
}
