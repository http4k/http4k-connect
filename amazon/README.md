# AWS Overview Guide

http4k-connect provides a standardised mechanism to connect to several AWS services. They all use the same mechanisms for authentication, which is what this page is about.

#### Auth

Authing into AWS services is possible with a few different mechanisms based on the environmental variables passed to your app. Under the covers, there is a `CredentialProvider` implementation which is switchable depending on your use-case:

#### Static AWS AccessKey/Secret authorisation uses:
- AWS_REGION
- AWS_ACCESS_KEY_ID
- AWS_SECRET_ACCESS_KEY
- AWS_SESSION_TOKEN

This is the default mechanism, so no special action is required:
```kotlin
val sqs = SQS.Http()
```

#### STS authorisation uses:
- AWS_REGION
- AWS_ACCESS_KEY_ID
- AWS_SECRET_ACCESS_KEY
- AWS_SESSION_TOKEN

This auth method uses the STS `AssumeRole` action to retrieve the rotating credentials from STS using auth from the environmental variables. This requires overriding the credentials provider used when constructing the client:
```kotlin
val sqs = SQS.Http(credentialsProvider = CredentialsProvider.STS())
```

#### STS WebIdentity authorisation uses:
- AWS_ROLE_ARN
- AWS_WEB_IDENTITY_TOKEN_FILE

This auth method uses the STS `AssumeRoleWithWebIdentity` action to retrieve the rotating credentials from STS using the Web Identity JWT from the file path contained in the env variable. This requires overriding the credentials provider used when constructing the client:

```kotlin
val sqs = SQS.Http(credentialsProvider = CredentialsProvider.STSWebIdentity())
```
