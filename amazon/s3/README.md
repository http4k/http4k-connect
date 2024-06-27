# S3

The S3 connector consists of 2 interfaces:

- `S3` for global operations, providing the following Actions:

    * CreateBucket
    * HeadBucket
    * ListBuckets

- `S3Bucket` for bucket level operations, providing the following Actions:

    * CopyObject
    * CreateObject
    * DeleteBucket
    * DeleteObject
    * DeleteObjectTagging
    * GetObject
    * GetObjectTagging
    * HeadObject
    * ListObjectsV2
    * PutObject
    * PutObjectTagging
    * RestoreObject

### Example usage

```kotlin
const val USE_REAL_CLIENT = false

fun main() {
    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeS3()

    val bucketName = BucketName.of("foobar")
    val bucketKey = BucketKey.of("keyName")
    val region = Region.of("us-east-1")

    // create global and bucket level clients
    val s3 = S3.Http({ AwsCredentials("accessKeyId", "secretKey") }, http.debug())
    val s3Bucket = S3Bucket.Http(bucketName, region, { AwsCredentials("accessKeyId", "secretKey") }, http.debug())

    // all operations return a Result monad of the API type
    val createResult: Result<Unit, RemoteFailure> = s3.createBucket(bucketName, region)
    createResult.valueOrNull()!!

    // we can store some content in the bucket...
    val putResult: Result<Unit, RemoteFailure> = s3Bucket.putObject(bucketKey, "hellothere".byteInputStream())
    putResult.valueOrNull()!!

    // and get back the content which we stored
    val getResult: Result<InputStream?, RemoteFailure> = s3Bucket.get(bucketKey)
    val content: InputStream = getResult.valueOrNull()!!
    println(content.reader().readText())
}
```

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat
Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a
performance factor.

### How the Fake works with bucket-level operations

S3 is a bit of a strange beast in that it each bucket gets its own virtual hostname. This makes running a Fake an
interesting challenge without messing around with DNS and hostname files.

This implementation supports both global and bucket level operations by inspecting the subdomain of the X-Forwarded-For
header, which is populated by the S3 client built into this module.

In the case of a missing header (if for instance a non-http4k client attempts to push some data into it without the
x-forwarded-for header), it creates a global bucket which is then used to store all of the data for these unknown
requests.

### Default Fake ports:

- Global: default port: 26467
- Bucket: default port: 42628

```
FakeS3().start()
```

### Connecting to a local S3 emulator

Services like [LocalStack](https://docs.localstack.cloud/user-guide/aws/s3/) or
[MinIO](https://min.io/docs/minio/container/index.html) can emulate AWS services locally.
However, for S3 bucket operations you either need to use a specific pre-configured bucket hostname 
like `http://<bucket-name>.s3.localhost.localstack.cloud:4566`, or you configure the `S3Bucket` to always 
perform path-style requests like this:

```kotlin
val s3Bucket = S3Bucket.Http(
    bucketName = bucketName, 
    bucketRegion = region,
    credentialsProvider = { credentials },
    overrideEndpoint = Uri.of("http://localhost:4566"),
    forcePathStyle = true // always use path-style requests
)
```

### Pre-Signed Requests

Http4k supports pre-signed requests with the generic `AwsRequestPreSigner` class.
However, `http4k-connect` provides a simplified interface for common S3 Bucket operations with the `S3BucketPresigner`.

```kotlin
fun main() {    
    // create pre-signer
    val preSigner = S3BucketPreSigner(
        bucketName = BucketName.of("foobar"),
        region = Region.of("us-east-1"),
        credentials = AwsCredentials("accessKeyId", "secretKey")
    )

    val key = BucketKey.of("keyName")
    
    // create a pre-signed PUT
    val put = preSigner.put(
        key = key,
        duration = Duration.ofMinutes(5), // how long the URL is valid for
        headers = listOf("content-type" to "application.json")  // add optional signed headers
    )
    println(put.uri)
    
    // create a pre-signed GET
    val get = preSigner.get(
        key = key,
        duration = Duration.ofMinutes(5)
    )
    println(get)

    // share these URIs to your clients so they can perform the operations without credentials
}
```
