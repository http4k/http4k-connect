# Simple Queue Service

The SQS connector provides the following Actions:

     *  CreateQueue
     *  DeleteQueue
     *  SendMessage
     *  ReceiveMessage
     *  DeleteMessage

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a performance factor.

Note that the FakeSQS is only suitable for very simple scenarios (testing and deployment for single consumer only) and does NOT implement real SQS semantics such as VisibilityTimeout or maximum number of retrieved messages (it delivers all undeleted messages to each consumer). Fake SQS queues are, as such, all inherently FIFO queues.

### Default Fake port: 37391

To start:
```
FakeSQS().start()
```
