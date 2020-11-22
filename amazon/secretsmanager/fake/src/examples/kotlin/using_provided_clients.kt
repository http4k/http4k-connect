import org.http4k.connect.Choice2
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.SecretId
import org.http4k.connect.amazon.secretsmanager.CreateSecret
import org.http4k.connect.amazon.secretsmanager.FakeSecretsManager
import org.http4k.connect.amazon.secretsmanager.GetSecret
import java.util.UUID

fun main() {
    val fakeSm = FakeSecretsManager()
    val client = fakeSm.client()
    val secretId = SecretId("a-secret-id")

    println(client.create(
        CreateSecret.Request("friendly name",
            UUID.randomUUID(),
            Choice2._1(Base64Blob.encoded("hello"))))
    )

    println(client.get(GetSecret.Request(secretId)))
}
