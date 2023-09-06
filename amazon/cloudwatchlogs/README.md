# CloudWatchLogs

The CloudWatchLogs connector provides the following Actions:

     *  **

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat
Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a
performance factor.

### Example usage

```kotlin
const val USE_REAL_CLIENT = false

fun main() {
    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeCloudWatch()

    // create a client
    val client =
        CloudWatchLogs.Http({ AwsCredentials("accessKeyId", "secretKey") }, http.debug())

    // all operations return a Result monad of the API type
    val result: Result<Unit, RemoteFailure> = client
}
```

### Default Fake port: 56514

To start:

```
FakeCloudWatchLogs().start()
```
