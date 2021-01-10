# KMS

The KMS connector provides the following Actions:

     *  CreateKey
     *  DescribeKey
     *  Decrypt
     *  Encrypt
     *  GetPublicKey
     *  ListKeys
     *  ScheduleKeyDeletion
     *  Sign
     *  Verify

### Example usage
```kotlin
const val USE_REAL_CLIENT = false

fun main() {
    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if(USE_REAL_CLIENT) JavaHttpClient() else FakeKMS()

    // create a client
    val client = KMS.Http(Region.of("us-east-1"), { AwsCredentials("accessKeyId", "secretKey") }, http.debug())

    // all operations return a Result monad of the API type
    val createdKeyResult: Result<KeyCreated, RemoteFailure> = client.createKey(ECC_NIST_P384, ENCRYPT_DECRYPT)
    val key: KeyCreated = createdKeyResult.valueOrNull()!!
    println(key)

    // we can encrypt some text...
    val encrypted: Encrypted = client.encrypt(keyId = key.KeyMetadata.KeyId, Base64Blob.encoded("hello"))
        .valueOrNull()!!
    println(encrypted.CiphertextBlob.decoded())

    // and decrypt it again!
    val decrypted: Decrypted = client.decrypt(keyId = key.KeyMetadata.KeyId, encrypted.CiphertextBlob).valueOrNull()!!
    println(decrypted.Plaintext.decoded())
}
```

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a performance factor.

The FakeKMS implementation currently does not properly encrypt/decrypt or sign/verify the contents of messages - it uses a trivially simple (and fast) reversible algorithm which simulates this functionality.

### Default Fake port: 45302

To start:
```
FakeKMS().start()
```
