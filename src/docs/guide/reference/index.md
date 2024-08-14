# http4k Connect module overview

- AWS
    - [AppRunner](./amazon/apprunner) -> `"org.http4k:http4k-connect-amazon-apprunner"` / `"org.http4k:http4k-connect-amazon-apprunner-fake"`
    - [CloudFront](./amazon/kms) -> `"org.http4k:http4k-connect-amazon-cloudfront"` / `"org.http4k:http4k-connect-amazon-cloudfront-fake"`
    - [CloudWatchLogs](./amazon/cloudwatchlogs) -> `"org.http4k:http4k-connect-amazon-cloudwatchlogs"` / `"org.http4k:http4k-connect-amazon-cloudwatchlogs-fake"`
    - [DynamoDb](./amazon/dynamodb) -> `"org.http4k:http4k-connect-amazon-dynamodb"` / `"org.http4k:http4k-connect-amazon-dynamodb-fake"`
    - [EventBridge](./amazon/eventbridge) -> `"org.http4k:http4k-connect-amazon-eventbridge"` / `"org.http4k:http4k-connect-amazon-eventbridge-fake"`
    - [Evidently](./amazon/evidently) -> `"org.http4k:http4k-connect-amazon-evidently"` / `"org.http4k:http4k-connect-amazon-evidently-fake"`
    - [Firehose](./amazon/firehose) -> `"org.http4k:http4k-connect-amazon-firehose"` / `"org.http4k:http4k-connect-amazon-firehose-fake"`
    - [IAM Identity Center](./amazon/iamidentitycenter) -> `"org.http4k:http4k-connect-amazon-iamidentitycenter"` / `"org.http4k:http4k-connect-amazon-iamidentitycenter-fake"`
    - [InstanceMetadataService](./amazon/instancemetadata) -> `"org.http4k:http4k-connect-amazon-instancemetadata"` / `"org.http4k:http4k-connect-amazon-instancemetadata-fake"`
    - [KMS](./amazon/kms) -> `"org.http4k:http4k-connect-amazon-kms"` / `"org.http4k:http4k-connect-amazon-kms-fake"`
    - [Lambda](./amazon/lambda) -> `"org.http4k:http4k-connect-amazon-lambda"` / `"org.http4k:http4k-connect-amazon-lambda-fake"`
    - [S3](./amazon/s3) -> `"org.http4k:http4k-connect-amazon-s3"` / `"org.http4k:http4k-connect-amazon-s3-fake"`
    - [SecretsManager](./amazon/secretsmanager) -> `"org.http4k:http4k-connect-amazon-secretsmanager"` / `"org.http4k:http4k-connect-amazon-secretsmanager-fake"`
    - [SES](./amazon/ses) -> `"org.http4k:http4k-connect-amazon-ses"` / `"org.http4k:http4k-connect-amazon-ses-fake"`
    - [SNS](./amazon/sns) -> `"org.http4k:http4k-connect-amazon-sns"` / `"org.http4k:http4k-connect-amazon-sns-fake"`
    - [SQS](./amazon/sqs) -> `"org.http4k:http4k-connect-amazon-sqs"` / `"org.http4k:http4k-connect-amazon-sqs-fake"`
    - [STS](./amazon/sts) -> `"org.http4k:http4k-connect-amazon-sts"` / `"org.http4k:http4k-connect-amazon-sts-fake"`
    - [SystemsManager](./amazon/systemsmanager) -> `"org.http4k:http4k-connect-amazon-systemsmanager"` / `"org.http4k:http4k-connect-amazon-systemsmanager-fake"`
- [GitHub V3, App, Callback](./github) -> `"org.http4k:http4k-connect-github"`
- Google Analytics
    - [GA4](./google/analytics-ga4) -> `"org.http4k:http4k-connect-google-analytics-ga4"` / `"org.http4k:http4k-connect-google-analytic-ga4-fake"`
    - [UA](./google/analytics-ua) -> `"org.http4k:http4k-connect-google-analytics-ua"` / `"org.http4k:http4k-connect-google-analytic-ua-fake"`
- Kafka
    - [Rest Proxy](kafka/rest) -> `"org.http4k:http4k-connect-kafka-rest"` / `"org.http4k:http4k-connect-kafka-rest-fake"`
    - [Schema Registry](kafka/schemaregistry) -> `"org.http4k:http4k-connect-kafka-schemaregistry"` / `"org.http4k:http4k-connect-kafka-schemaregistry-fake"`
- [Mattermost Webhook](./mattermost) -> `"org.http4k:http4k-connect-mattermost"` / `"org.http4k:http4k-connect-mattermost-fake"`
- AI:
    - [LangChain4J](./ai/langchain) -> `"org.http4k:http4k-connect-ai-langchain"`
    - [LmStudio](./ai/lmstudio) -> `"org.http4k:http4k-connect-ai-lmstudio"` / `"org.http4k:http4k-connect-ai-lmstudio-fake"`
    - [OpenAI](./ai/openai) -> `"org.http4k:http4k-connect-ai-openai"` / `"org.http4k:http4k-connect-ai-openai-plugin"`/ `"org.http4k:http4k-connect-ai-openai-fake"`
    - [Ollama](./ai/ollama) -> `"org.http4k:http4k-connect-ai-ollama"` / `"org.http4k:http4k-connect-ai-ollama-fake"`
- [Example Template](./example) -> `"org.http4k:http4k-connect-example"` / `"org.http4k:http4k-connect-example-fake"`

## Supported Storage backends (named http4k-connect-storage-{technology}>)

- [In-Memory](./storage/core) (included with all Fakes)
- [File-Based](./storage/core) (included with all Fakes)
- [JDBC](./storage/jdbc) -> `org.http4k:http4k-connect-storage-jdbc`
- [Redis](./storage/redis) -> `org.http4k:http4k-connect-storage-redis`
- [S3](./storage/s3) -> `org.http4k:http4k-connect-storage-s3`
