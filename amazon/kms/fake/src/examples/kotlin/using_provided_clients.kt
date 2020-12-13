import dev.forkhandles.result4k.valueOrNull
import org.http4k.connect.amazon.kms.FakeKMS
import org.http4k.connect.amazon.kms.action.CreateKey
import org.http4k.connect.amazon.kms.action.Decrypt
import org.http4k.connect.amazon.kms.action.Encrypt
import org.http4k.connect.amazon.model.Base64Blob

fun main() {
    val fakeKms = FakeKMS()
    val client = fakeKms.client()

    val key = client(CreateKey()).valueOrNull()!!
    val encrypted = client(Encrypt(KeyId = key.KeyMetadata.KeyId, Base64Blob.encoded("hello"))).valueOrNull()!!
    println(encrypted.CiphertextBlob.decoded())
    val decrypted = client(Decrypt(KeyId = key.KeyMetadata.KeyId, encrypted.CiphertextBlob)).valueOrNull()!!
    println(decrypted.Plaintext.decoded())

}
