# Simple Email Service

The SES connector provides the following Actions:

* SendEmail

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat
Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a
performance factor.

### Example usage

### Default Fake port: 59920

To start:

```
FakeSES().start()
```
