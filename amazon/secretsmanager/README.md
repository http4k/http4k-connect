# Secrets Manager

### Installation

```kotlin
dependencies {
    implementation(platform("org.http4k:http4k-connect-bom:5.20.0.0"))
    implementation("org.http4k:http4k-connect-amazon-secretsmanager")
    implementation("org.http4k:http4k-connect-amazon-secretsmanager-fake")
}
```


The Secrets Manager connector provides the following Actions:

     *  CreateSecret
     *  DeleteSecret
     *  GetSecretValue
     *  ListSecrets
     *  PutSecretValue
     *  UpdateSecret

### Example usage

```kotlin
const val USE_REAL_CLIENT = false

fun main() {
    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeSecretsManager()

    // create a client
    val client =
        SecretsManager.Http(Region.of("us-east-1"), { AwsCredentials("accessKeyId", "secretKey") }, http.debug())

    val secretId = SecretId.of("a-secret-id")

    // all operations return a Result monad of the API type
    val createdSecretResult: Result<CreatedSecret, RemoteFailure> =
        client.createSecret(secretId.value, UUID.randomUUID(), "value")
    println(createdSecretResult.valueOrNull())

    // get the secret value back
    println(client.getSecretValue(secretId).valueOrNull())
}
```

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat
Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a
performance factor.

### Default Fake port: 58194

To start:

```
FakeSecretsManager().start()
```
