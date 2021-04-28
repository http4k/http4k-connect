# Changelog

This list is not intended to be all-encompassing - it will document major and breaking API 
changes with their rationale when appropriate. Given version `A.B.C.D`, breaking changes are to be expected in version number increments where changes in the `A` or `B` sections:

### v3.1.1.0
- **http4k-connect-amazon-cognito** - New module (WIP). Base actions for user client and pool creation are implemented, no fake as yet.

### v3.1.0.1
- **http4k-connect-amazon-dynamodb** : Removed non-nullable field on ConsumedCapacity.

### v3.1.0.0
- **http4k-connect-amazon-s3*** : Add support for path-based bucket operations (ie. buckets with `.` in the name)
- **http4k-connect-amazon-s3*** : [Rename break] Renamed `*Key` actions to match S3 API (now `*Object`)
- **http4k-connect-amazon-s3*** : [Slight break] Add headers to `PutObject`.

### v3.0.3.0
- **http4k-connect-*** : Add convenience functions for getting AWS environmental variables from an http4k Environment object.

### v3.0.2.0
- **http4k-connect-*** : Upgrade http4k.

### v3.0.1.0
- **http4k-connect-*** : Add Moshi serializers for enums, making them compatible with GraalVM

### v3.0.0.0
- **http4k-connect-*** : Major repackage of all model classes. Model package has been normalised to `org.http4k.connect.amazon.<system>.model`. All non-top level message objects have been moved from the `org.http4k.connect.amazon.<system>.action` package into `org.http4k.connect.amazon.<system>.model`. This is probably very annoying, and apologies in advance - hence the major version uptick. We are not proud of ourselves, but it needed to be done for our future plans... Also imports of generated adapter methods may need to be altered as some of them were in teh wrong place.

### v2.23.0.0
- **http4k-connect-amazon-dynamodb** : [Slight break] Repackaging work of item types to reuse them for Dynamo event marshalling.

### v2.22.1.0
- **http4k-connect-amazon-dynamodb** : Support Dynamo Events in marshalling layer.

### v2.22.0.1
- **http4k-connect-amazon-dynamodb** : Fix incorrectly specified data type for OffsetDateTime attributes.

### v2.22.0.0
- **http4k-connect-amazon-dynamodb** : [Breaking] Change `value()` method on `Attribute` to be typed. This only affects you if you are using `values4k` value classes for column mappings.

### v2.21.1.1
- **http4k-connect-amazon-dynamodb** : Fix long value stored as a string.

### v2.21.1.0
- **http4k-connect-*** : Add default values to all nullable response message fields. This is better for when stubbing/mocking out the responses.

### v2.21.0.0
- **http4k-connect-*** : [Breaking] Repackaged Pagination classes (not just Amazon anymore).
- **http4k-connect-*** : [Breaking] Added pagination of results to relevant actions using `xyzPaginated()` actions. Removed usage of `Listing` classes. This is a more convenient API to use and is consistent throughout all modules.

### v2.21.1.1
- **http4k-connect-amazon-dynamodb** : Fix bug with Long data type. @H/T @ToastShaman for the tip off.

### v2.20.1.0
- **http4k-connect-amazon-dynamodb** : Added pagination of results

### v2.20.0.0
- **http4k-connect-amazon-dynamodb** : More making API nicer and typesafe.

### v2.19.0.0
- **http4k-connect-amazon-*** : [Breaking] Changed generated helper functions to not interfere with the names of the parameters. Simple rename will work here.
- **http4k-connect-*** : Friendlify JavaDocs.

### v2.18.1.0
- **http4k-connect-amazon-cloudfront** : New module.
- **http4k-connect-amazon-cloudfront-fake* : New module.

### v2.18.0.0
- **http4k-connect-amazon-dynamodb** : Further tweaking of the Item and Key mapping typealiases to make API easier to use.

### v2.17.0.0
- **http4k-connect-amazon-dynamodb** : Reworked DynamoDb API to be typesafe, tightened up types in responses, added Scan.

### v2.16.0.0
- **http4k-connect-amazon-dynamodb** : New client module. No fake as yet.
- **http4k-connect-amazon-*** : [Break] Rename `Base64Blob.encoded()` -> `Base64Blob.encode()` for clarity.

### v2.15.4.0
- **http4k-connect-github** : Add infra for main GitHub adapter. No custom actions implemented yet.

### v2.15.3.0
- **http4k-connect-github** : New module containing only basic callback infrastructure and Filters for checking requests.

### v2.15.2.0
- **http4k-connect-*** : upgrade http4k. This should Fix #17 (Enable custom domain in S3).

### v2.15.1.0
- **http4k-connect-*** : upgrade http4k, Kotlin.

### v2.15.0.1
- Switch to Maven Central publishing as first options

### v2.15.0.0
- **http4k-connect-google-analytics** : [Break] Harmonised interface with other adapters. TrackingId now moved 
to individual requests

### v2.14.2.0
- **http4k-connect-*** : upgrade http4k, kotlin, others

### v2.14.1.0
- **http4k-connect-*** : upgrade http4k
- **http4k-connect-kapt-generator** : Un-hardcode result type as per Action interface. 

### v2.14.0.0
- **http4k-connect-*** : [Breaking] Changed Result type on Action to be generic to support other programming models. This will only affect users who are implementing their own adapters. To fix, change: 
```kotlin
interface MyAdapter<R> : Action<R>
// to 
interface MyAdapter<R> : Action<Result<R, RemoteFailure>>
```

### v2.13.0.1
- **http4k-connect-amazon-s3-fake* : Send response XML as well as status code on errors.

### v2.13.0.0
- **http4k-connect-*** : Rejig of dependencies to be consistent.

### v2.12.0.0
- **http4k-connect-storage-core** : New module, containing storage abstractions which can be used without the fakes.

### v2.11.0.0
- **http4k-connect-amazon-sns** : New module.
- **http4k-connect-amazon-sns-fake* : New module.
- **http4k-connect-** : Make all action classes Data classes so they are test friendly
- **http4k-connect-amazon-sqs** : [Breaking] Tags is now a `List<Tag>` instead of a `Map<String, String>`.

### v2.10.0.0
- **http4k-connect-amazon-** : Add convenience functions to create clients from the system environment. 
- **http4k-connect-amazon-** : Removed unused Payload type for various clients.
- **http4k-connect-*** : Upgrade values4k and http4k

### v2.9.2.0
- **http4k-connect-amazon-** : Add convenience methods for constructing AWS clients

### v2.9.1.0
- **http4k-connect-amazon-** : Expose Moshi to client API users for JSON-based systems

### v2.9.0.0
- **http4k-connect-amazon-sqs** : Fixed SQS MessageAttributes as API is not as advertised...
- **http4k-connect-amazon-fake** : Extracting out endpoints for easier extension.

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
