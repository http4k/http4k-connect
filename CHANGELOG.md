# Changelog

This list is not intended to be all-encompassing - it will document major and breaking API 
changes with their rationale when appropriate. Given version `A.B.C.D`, breaking changes are to be expected in version number increments where changes in the `A` or `B` sections:

### v2.8.0.0
- **http4k-connect-*** : Upgrade to http4k 4.X.X.X.

### v2.7.1.0
- **http4k-connect-amazon-systemsmanager** : Refined model.
- **http4k-connect-amazon-*** : Fixed handling of ARNs.

### v2.7.0.0
- **http4k-connect-amazon-*** : Refined ARN model.
- **http4k-connect-amazon-s3** : Fix Delete Bucket action.

### v2.6.0.0
- **http4k-connect-amazon-*** : API improvements for all AWS services.
- **http4k-connect-*** : `defaultPort()` -> `defaultPort`

### v2.5.1.0
- **http4k-connect-amazon-lambda* : Expose AutoMarshalling in extension function.

### v2.5.0.0
- **http4k-connect-amazon-lambda* : Expose `AutoMarshalling` for invoking functions.

### v2.4.0.0
- **http4k-connect-** : Remove need for AWSCredentialScope - just use Region instead since each service already knows the scope required.

### v2.3.2.0
- **http4k-connect-amazon-sqs* : New module.
- **http4k-connect-amazon-sqs-fake* : New module. See README for limitations of FakeSQS.
- **http4k-connect-amazon-sts* : Added STSCredentialsProvider to refresh credentials when required.

### v2.3.1.1
- **http4k-connect-** : Fix #11 thread safety of DocumentBuilderFactory.

### v2.3.1.0
- **http4k-connect-amazon-lambda* : New module. Support for invoking AWS Lambda functions.
- **http4k-connect-amazon-lambda-fake* : New module. Includes FakeLambda runtime to run/deploy named HttpHandlers into.

### v2.3.0.0
- **http4k-connect-** : Use Kotshi generated adapters instead of Kotlin Reflection, allowing removal of large Kotlin Reflection JAR. Note that the Kotlin-reflect dependency must be explicitly excluded due to transitivity in your projects.

### v2.2.2.0
- **http4k-connect-** : Generate and ship extension functions for all actions. Rename `S3.Bucket` to `S3Bucket`.

### v2.2.1.0
- **http4k-connect-** : Ship Javadoc.

### v2.2.0.0
- **http4k-connect-** : Repackage all action classes.

### v2.1.0.0
- **http4k-connect-** : Repackage all action classes.

### v2.0.2.1
- **http4k-connect-** : Switch all interfaces to use new `invoke()` mechanism.

### v1.1.0.1
- **http4k-connect-** : Upgrade http4k and Values4k.

### v1.0.1.0
- **http4k-connect-amazon-kms-fake** : Simplify signing.

### v1.0.0.0
- **http4k-connect-amazon-kms** : New client module.
- **http4k-connect-amazon-kms-fake** : New client fake module.
- **http4k-connect-amazon-s3** : New client module.
- **http4k-connect-amazon-s3-fake** : New client fake module.
- **http4k-connect-amazon-secretsmanager** : New client module.
- **http4k-connect-amazon-secretsmanager-fake** : New client fake module.
- **http4k-connect-amazon-systemsmanager** : New client module.
- **http4k-connect-amazon-systemsmanager-fake** : New client fake module.
- **http4k-connect-google-analytics** : New client module.
- **http4k-connect-storage-http** : New storage module.
- **http4k-connect-storage-jdbc** : New storage module.
- **http4k-connect-storage-redis** : New storage module.
- **http4k-connect-storage-s3** : New storage module.

### v0.20.0.0
- Initial release.
