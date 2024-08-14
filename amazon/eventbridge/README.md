# EventBridge

### Installation

```kotlin
dependencies {
    implementation(platform("org.http4k:http4k-connect-bom:5.20.0.0"))
    implementation("org.http4k:http4k-connect-amazon-eventbridge")
    implementation("org.http4k:http4k-connect-amazon-eventbridge-fake")
}
```


The EventBridge connector provides the following Actions:
     *  CreateEventBus
     *  DeleteEventBus
     *  DescribeEventBus
     *  PutEvents

### Example usage
```kotlin
```

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a performance factor.

### Default Fake port: 13577

To start:
```
FakeEventBridge().start()
```
