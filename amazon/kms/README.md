# KMS

The KMS connector provides the following Actions:

     *  CreateKey
     *  DescribeKey
     *  Decrypt
     *  Encrypt
     *  GetPublicKey
     *  ListKeys
     *  ScheduleKeyDeletion
     *  Sign
     *  Verify

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a performance factor.

The FakeKMS implementation currently does not properly encrypt/decrypt or sign/verify the contents of messages - it uses a trivially simple (and fast) reversible algorithm which simulates this functionality.

### Default Fake port: 45302

To start:
```
FakeKMS().start()
```
