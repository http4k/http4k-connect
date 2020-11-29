# Secrets Manager

The Secrets Manager connector provides the following functionality:
- CRUD Secrets

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a performance factor.

### Default Fake port: 58194

To start:
```
FakeSecretsManager().start()
```
