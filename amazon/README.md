# AWS Services

http4k-connect provides a standardised mechanism to connect to the following AWS services:

- CloudFront
- Cognito
- DynamoDB
- KMS
- Lambda
- S3
- SecretsManager
- SNS
- SQS
- STS
- SystemsManager

#### Auth

Authing into AWS services is possible with a few different mechanisms based on the environmental variables passed to your app:

#### Static authorisation uses:
- AWS_REGION
- AWS_ACCESS_KEY_ID
- AWS_SECRET_ACCESS_KEY
- AWS_SESSION_TOKEN

The simplest way to activate this is to use the http4k typesafe configuration from the http4k-cloudnative module:
```kotlin
val sqs = SQS.Http(Environment.ENV)
```

#### STS authorisation uses:
- AWS_REGION
- AWS_ACCESS_KEY_ID
- AWS_SECRET_ACCESS_KEY
- AWS_SESSION_TOKEN

This auth method uses the STS `AssumeRole` action to retrieve the rotating credentials using the environmental variables. This requires overriding the credentials provider used when constructing the adapter:
```kotlin
val sqs = SQS.Http(Environment.ENV, CredentialsProvider.STS(ENV))
```

#### STS WebIdentity authorisation uses:
- AWS_ROLE_ARN
- AWS_WEB_IDENTITY_TOKEN_FILE

This auth method uses the STS `AssumeRoleWithWebIdentity` action to retrieve the rotating credentials using the Web Identity JWT from the file path contained in the env variable. This requires overriding the credentials provider used when constructing the adapter:

```kotlin
val sqs = SQS.Http(Environment.ENV, CredentialsProvider.STSWebIdentity(ENV))
```
