# Instance Metadata Service

The [Instance Metadata Service](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-instance-metadata.html) V1 connector provides the following Actions:

     *  GetAmiId
     *  GetHostName
     *  GetInstanceIdentityDocument
     *  GetInstanceType
     *  GetLocalHostName
     *  GetLocalIpv4
     *  GetPublicHostName
     *  GetPublicIpv4
     *  GetSecurityCredentials
     *  ListSecurityCredentials

### Example usage

```kotlin
const val USE_REAL_CLIENT = false

fun main() {
    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeInstanceMetadataService()

    // create a client
    val client = InstanceMetadataService.Http(http.debug())

    // get local ip address
    val localIp = client.getLocalIpv4()
    println(localIp)

    // get identity document
    val identityDocument = client.getInstanceIdentityDocument()
    println(identityDocument)
}
```

### Credentials Provider

The Instance Metadata Service also offers a `CredentialsProvider`.
If the application is running inside an Amazon EC2 environment,
this provider can authorize AWS requests using credentials from the instance profile.

```kotlin
fun main() {    
    // build a credentials provider that will attempt to load AWS credentials from the EC2's instance profile
    val credentialsProvider = CredentialsProvider.Ec2InstanceProfile()
    
    // build a client that will authorize requests with the instance profile credentials
    val s3 = S3.Http(credentialsProvider)
    
    // send a request
    val buckets = s3.listBuckets().successValue()
    println(buckets)
}
```

### Default Fake port: 63407

To start:

```
FakeInstanceMetadataService().start()
```
