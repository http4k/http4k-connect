# http4k Connect module overview

### Cloud Services

| Vendor     | System                                            | In-Memory Fake | Notes                                  |
|------------|---------------------------------------------------|----------------|----------------------------------------|
| AWS        | [AppRunner](./amazon/apprunner)                   | ✅              |                                        |
| AWS        | [CloudFront](./amazon/cloudfront)                 | ✅              |                                        |
| AWS        | [Cloudwatch Logs](./amazon/cloudwatchlogs)        | ✅              |                                        |
| AWS        | [DynamoDb](./amazon/dynamodb)                     | ✅              |                                        |
| AWS        | [EventBridge](./amazon/eventbridge)               | ✅              |                                        |
| AWS        | [Evidently](./amazon/evidently)                   | ✅              |                                        |
| AWS        | [Firehose](./amazon/firehose)                     | ✅              |                                        |
| AWS        | [IAM Identity Center](./amazon/iamidentitycenter) | ✅              |                                        |
| AWS        | [Instance Metadata](./amazon/instancemetadata)    | ✅              |                                        |
| AWS        | [KMS](./amazon/kms)                               | ✅              |                                        |
| AWS        | [Lambda](./amazon/lambda)                         | ✅              |                                        |
| AWS        | [S3](./amazon/s3)                                 | ✅              |                                        |
| AWS        | [Secrets Manager](./amazon/secretsmanager)        | ✅              |                                        |
| AWS        | [SES](./amazon/ses)                               | ✅              |                                        |
| AWS        | [SNS](./amazon/sns)                               | ✅              |                                        |
| AWS        | [SQS](./amazon/sqs)                               | ✅              |                                        |
| AWS        | [STS](./amazon/sts)                               | ✅              |                                        |
| AWS        | [Systems Manager](./amazon/systemsmanager)        | ✅              |                                        |
| GitHub     | [V3 API](./github)                                | ❌              | Adapter Shell and WebHook Signing only |
| GitLab     | [API](./gitlab)                                   | ❌              | Adapter Shell and WebHook Signing only |
| Google     | [Analytics GA4](./google/analytics-ga4)           | ✅              |                                        |
| Google     | [Analytics UA](./google/analytics-ua)             | ✅              |                                        |
| Kafka      | [Rest Proxy](./kafka/rest)                        | ✅              |                                        |
| Kafka      | [Schema Registry](./kafka/schemaregistry)         | ✅              |                                        |
| Mattermost | [WebHook](./mattermost)                           | ❌              |                                        |

### AI Services

| Vendor      | System                  | In-Memory Fake | Notes                                            |
|-------------|-------------------------|----------------|--------------------------------------------------|
| LangChain4J | [Adapters](./langchain) | ❌              | Adapters to be plugged into LangChains           |
| LM Studio   | [API](./lmstudio)       | ✅              |                                                  |
| Ollama      | [API](./ollama)         | ✅              | Includes content generators and image generation |
| Open AI     | [API](./openai)         | ✅              | Includes content generators and image generation |

### Storage Implementations

| Implementation               | Notes                   |
|------------------------------|-------------------------|
| [In-Memory](./storage/core)  | Included with all Fakes |
| [File-Based](./storage/core) | Included with all Fakes |
| [JDBC](./storage/jdbc)       |                         |
| [Redis](./storage/redis)     |                         |
| [S3](./storage/s3)           |                         |
