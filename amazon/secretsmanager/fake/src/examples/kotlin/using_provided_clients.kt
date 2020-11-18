import org.http4k.connect.amazon.Base64Blob
import org.http4k.connect.amazon.secretsmanager.FakeSecretsManager
import org.http4k.connect.amazon.secretsmanager.GetSecretValue
import org.http4k.connect.amazon.secretsmanager.PutSecretValue
import org.http4k.connect.amazon.secretsmanager.SecretId
import org.http4k.connect.amazon.secretsmanager.VersionId
import org.http4k.connect.amazon.secretsmanager.VersionStage

fun main() {
    val fakeSm = FakeSecretsManager()
    val client = fakeSm.client()
    val secretId = SecretId("")
    val stage = VersionStage("stage")

    println(client.put(
        PutSecretValue.Request("token",
            Base64Blob.encoded("hello"),
            secretId, "",
            listOf(stage))))
    println(client.get(GetSecretValue.Request(secretId, VersionId("123"), stage)))
}
