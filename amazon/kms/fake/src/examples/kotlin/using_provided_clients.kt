import dev.forkhandles.result4k.valueOrNull
import org.http4k.connect.amazon.kms.CreateKeyRequest
import org.http4k.connect.amazon.kms.DecryptRequest
import org.http4k.connect.amazon.kms.EncryptRequest
import org.http4k.connect.amazon.kms.FakeKMS
import org.http4k.connect.amazon.model.Base64Blob

fun main() {
    val fakeKms = FakeKMS()
    val client = fakeKms.client()

    val key = client.create(CreateKeyRequest()).valueOrNull()!!
    val encrypted = client.encrypt(EncryptRequest(KeyId = key.KeyMetadata.KeyId, Base64Blob.encoded("hello"))).valueOrNull()!!
    println(encrypted.CiphertextBlob.decoded())
    val decrypted = client.decrypt(DecryptRequest(KeyId = key.KeyMetadata.KeyId, encrypted.CiphertextBlob)).valueOrNull()!!
    println(decrypted.Plaintext.decoded())

}
