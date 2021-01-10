# Systems Manager

The Systems Manager connector provides the following Actions:

     *  DeleteParameter
     *  GetParameter
     *  PutParameter

### Example usage

```kotlin
const val USE_REAL_CLIENT = false

fun main() {
    val paramName = SSMParameterName.of("name")

    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeSystemsManager()

    // create a client
    val client =
        SystemsManager.Http(Region.of("us-east-1"), { AwsCredentials("accessKeyId", "secretKey") }, http.debug())

    // all operations return a Result monad of the API type
    val putParameterResult: Result<PutParameterResult, RemoteFailure> =
        client.putParameter(paramName, "value", ParameterType.String)
    println(putParameterResult)

    // get the parameter back again
    println(client.getParameter(paramName))
}
```

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat
Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a
performance factor.

### Default Fake port: 42551

To start:

```
FakeSecretsManager().start()
```
