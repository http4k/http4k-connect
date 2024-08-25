# Simple Queue Service

### Installation

```kotlin
dependencies {
    implementation(platform("org.http4k:http4k-connect-bom:5.22.1.0"))
    implementation("org.http4k:http4k-connect-amazon-sqs")
    implementation("org.http4k:http4k-connect-amazon-sqs-fake")
}
```


The SQS connector provides the following Actions:

     *  CreateQueue
     *  DeleteMessage
     *  DeleteQueue
     *  GetQueueAttributes
     *  ListQueues
     *  ReceiveMessage
     *  SendMessage

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat
Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a
performance factor.

### Example usage

```kotlin
const val USE_REAL_CLIENT = false

fun main() {
    val region = Region.of("us-east-1")
    val queueName = QueueName.of("myqueue")
    val queueArn = ARN.of(SQS.awsService, region, AwsAccount.of("000000001"), queueName)

    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeSQS()

    // create a client
    val client = SQS.Http(region, { AwsCredentials("accessKeyId", "secretKey") }, http.debug())

    // all operations return a Result monad of the API type
    val createdQueueResult: Result<CreatedQueue, RemoteFailure> = client.createQueue(queueName, emptyMap(), emptyMap())
    println(createdQueueResult.valueOrNull()!!)

    // send a message
    println(client.sendMessage(queueArn, "hello"))

    // and receive it..
    println(client.receiveMessage(queueArn))
}
```

Note that the FakeSQS is only suitable for very simple scenarios (testing and deployment for single consumer only) and
does NOT implement real SQS semantics such as VisibilityTimeout or maximum number of retrieved messages (it delivers all
undeleted messages to each consumer). Fake SQS queues are, as such, all inherently FIFO queues.

### Default Fake port: 37391

To start:

```
FakeSQS().start()
```
