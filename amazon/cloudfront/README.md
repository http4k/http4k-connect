# CloudFront

### Installation

```kotlin
dependencies {
    implementation(platform("org.http4k:http4k-connect-bom:5.22.1.0"))
    implementation("org.http4k:http4k-connect-amazon-cloudfront")
    implementation("org.http4k:http4k-connect-amazon-cloudfront-fake")
}
```


The CloudFront connector provides the following Actions:

     *  CreateInvalidation

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat
Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a
performance factor.

### Example usage

```kotlin
const val USE_REAL_CLIENT = false

fun main() {
    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeCloudFront()

    // create a client
    val client =
        CloudFront.Http({ AwsCredentials("accessKeyId", "secretKey") }, http.debug())

    // all operations return a Result monad of the API type
    val result: Result<Unit, RemoteFailure> = client
        .createInvalidation(DistributionId.of("a-distribution-id"), listOf("/path"), 1, random())
}
```

### Default Fake port: 15420

To start:

```
FakeCloudFront().start()
```
