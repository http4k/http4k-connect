# Kafka HTTP Proxy

The main `KafkaHttpProxy` connector provides the following Actions:

- CommitOffsets
- ConsumeRecords
- CreateConsumer
- DeleteConsumer
- ProduceMessages
- SubscribeToTopics

In addition, you can use a `KafkaHttpProxyConsumer` which provides the following Actions:

- CommitOffsets
- ConsumeRecords
- Delete
- SubscribeToTopics

## # Fake

The Fake provides the following endpoints, which is enough for basic consumer lifecycle and production and consumption
of records. Note that consumers by default will start at the start of the topic stream, although they can be committed
to.

- CommitOffsets
- ConsumeRecords
- CreateConsumer
- DeleteConsumer
- ProduceMessages
- SubscribeToTopics

`http://<server:port>/<user pool id>/.well-known/jwks.json`

### Default Fake port: 12332

To start:

```
FakeKafkaHttpProxy().start()
```
