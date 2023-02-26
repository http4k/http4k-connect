# S3

The S3 connector consists of 2 interfaces:

- `S3` for global operations, providing the following Actions:

    * CreateBucket
    * HeadBucket
    * ListBuckets

- `S3.Bucket` for bucket level operations, providing the following Actions:

    * CopyObject
    * CreateObject
    * DeleteBucket
    * DeleteObject
    * GetObject
    * HeadObject
    * ListObjectsV2
    * PutObject

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

S3 is a bit of a strange beast in that it each bucket gets it's own virtual hostname. This makes running a Fake an
interesting challenge without messing around with DNS and hostname files.

This implementation supports both global and bucket level operations by inspecting the subdomain of the X-Forwarded-For
header, which is populated by the S3 client built into this module.

In the case of a missing header (if for instance a non-http4k client attempts to push some data into it without the
x-forwarded-for header, it creates a global bucket which is then used to store all of the data for these unknown
requests.

### Default Fake ports:

- Global: default port: 26467
- Bucket: default port: 42628

```
FakeS3().start()
```
