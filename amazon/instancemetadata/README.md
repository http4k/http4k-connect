# Instance Metadata Service

### Installation

```kotlin
dependencies {
    implementation(platform("org.http4k:http4k-connect-bom:5.20.0.0"))
    implementation("org.http4k:http4k-connect-amazon-instancemetadata")
    implementation("org.http4k:http4k-connect-amazon-instancemetadata-fake")
}
```


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
    val sns = SNS.Http(Region.US_EAST_1, credentialsProvider)

    // send a request
    val topics = sns.listTopics()
    println(topics)
}
```

:warning: The `Ec2InstanceProfile` provider should always be last in the chain,
since it will time out if not in an Amazon EC2 environment.


### Region Provider ###

The Instance Metadata Service also offers a `RegionProvider`.
If the application is running inside an Amazon EC2 environment,
this provider can detect the current AWS region.

```kotlin
fun main() {
    // we can connect to the real service or the fake (drop in replacement)
    val imdsHttp: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeInstanceMetadataService()
    val snsHttp: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeSNS()

    /*
     * Build a RegionProvider chain with the following steps:
     * 1. Try to get region from AWS_REGION environment variable
     * 2. Try to get region from profile credentials file
     * 3. Try to get region from EC2 Instance Metadata Service
     */
    val regionProviderChain = RegionProvider.Environment(Environment.ENV) orElse
        RegionProvider.Profile(Environment.ENV) orElse
        RegionProvider.Ec2InstanceProfile(imdsHttp)

    // Invoking the chain will return a region if one was found
    val optionalRegion: Region? = regionProviderChain()
    println(optionalRegion)

    // orElseThrow will return a region or throw an exception if onr was not found
    val region: Region = regionProviderChain.orElseThrow()
    println(region)

    // create and use an Amazon client with the resolved region
    val sns = SNS.Http(region, { fakeAwsCredentials }, snsHttp)
    val topics = sns.listTopics()
    println(topics)
}
```

:warning: The `Ec2InstanceProfile` provider should always be last in the chain,
since it will time out if not in an Amazon EC2 environment.


### Default Fake port: 63407

To start:

```
FakeInstanceMetadataService().start()
```
