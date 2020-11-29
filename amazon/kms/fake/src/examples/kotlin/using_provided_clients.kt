import dev.forkhandles.result4k.valueOrNull
import org.http4k.connect.amazon.kms.CreateKey
import org.http4k.connect.amazon.kms.Decrypt
import org.http4k.connect.amazon.kms.Encrypt
import org.http4k.connect.amazon.kms.FakeKMS
import org.http4k.connect.amazon.model.Base64Blob

fun main() {
    val fakeKms = FakeKMS()
    val client = fakeKms.client()

    val key = client.create(CreateKey.Request()).valueOrNull()!!
    val encrypted = client.encrypt(Encrypt.Request(KeyId = key.KeyMetadata.KeyId, Base64Blob.encoded("hello"))).valueOrNull()!!
    println(encrypted.CiphertextBlob.decoded())
    val decrypted = client.decrypt(Decrypt.Request(KeyId = key.KeyMetadata.KeyId, encrypted.CiphertextBlob)).valueOrNull()!!
    println(decrypted.Plaintext.decoded())

}
