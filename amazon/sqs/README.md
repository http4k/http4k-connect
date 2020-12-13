# Simple Queue Service

The SQS connector provides the following functionality:

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a performance factor.

### Default Fake port: 37391

To start:
```
FakeSQS().start()
```
