import dev.forkhandles.result4k.valueOrNull
import org.http4k.connect.amazon.kms.FakeKMS
import org.http4k.connect.amazon.kms.action.CreateKey
import org.http4k.connect.amazon.kms.decrypt
import org.http4k.connect.amazon.kms.encrypt
import org.http4k.connect.amazon.model.Base64Blob

fun main() {
    val fakeKms = FakeKMS()
    val client = fakeKms.client()

    val key = client(CreateKey()).valueOrNull()!!
    val encrypted = client.encrypt(keyId = key.KeyMetadata.KeyId, Base64Blob.encoded("hello")).valueOrNull()!!
    println(encrypted.CiphertextBlob.decoded())
    val decrypted = client.decrypt(keyId = key.KeyMetadata.KeyId, encrypted.CiphertextBlob).valueOrNull()!!
    println(decrypted.Plaintext.decoded())

}
