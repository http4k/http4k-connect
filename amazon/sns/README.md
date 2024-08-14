# Simple Notification Service

```kotlin
dependencies {
    implementation(platform("org.http4k:http4k-connect-bom:5.20.0.0"))
    implementation("org.http4k:http4k-connect-amazon-sns")
    implementation("org.http4k:http4k-connect-amazon-sns-fake")
}
```


The SNS connector provides the following Actions:

* CreateTopic
* DeleteTopic
* ListTopics
* Publish
* PublishBatch

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat
Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a
performance factor.

### Example usage

### Default Fake port: 58430

To start:

```
FakeSNS().start()
```
