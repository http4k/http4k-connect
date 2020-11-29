<hr/>
<img src="logo.png" alt="http4k-connect"/>
<hr/>

![https://bintray.com/http4k/maven/http4k-connect-bom/_latestVersion](https://api.bintray.com/packages/http4k/maven/http4k-connect-bom/images/download.svg)
![.github/workflows/build.yaml](https://github.com/http4k/http4k-connect/workflows/.github/workflows/build.yaml/badge.svg)
![.github/workflows/release.yaml](https://github.com/http4k/http4k-connect/workflows/.github/workflows/release.yaml/badge.svg)
![http://kotlinlang.org](https://img.shields.io/badge/kotlin-1.4-blue.svg)

http4k-connect is a set of lightweight API libraries for connecting to popular third-party services using http4k compatible APIs, along with Fake implementations for usage during testing and Storage implementations. These all utilise the uniform `Server as a Function` 
`HttpHandler` interface exposed by [http4k](https://http4k.org), so you can:
 
1. Take advantage of the simple and powerful SaaF model and APIs used in http4k.
1. Plug everything together completely in-memory and take advantage of this powerful model.
1. Have access to the underlying HTTP clients (and hence add metrics or logging).
1, Run stateful Fake implementations of 3rd party systems locally or in test environments.

Although centered around usage in http4k-based projects, http4k-connect does not require this and the libraries are usable from any JVM application.

## Rationale
Although convenient, many client libraries introduce many heavyweight dependencies or contain a plethora of non-required functionality, which can have a large effect on binary size. As an alternative, http4k-connect provides lightweight versions of popular APIs covering standard use-cases.

## Installation
```groovy
dependencies {
    // install the plaform...
    implementation platform("org.http4k:http4k-connect-bom:1.1.0.0")

    // ...then choose a client
    implementation "org.http4k:http4k-connect-amazon-s3"

    // ...a fake for testing
    testImplementation "org.http4k:http4k-connect-amazon-s3-fake"

    // ...and a storage backend (optional)
    testImplementation "org.http4k:http4k-storage-redis"
}
```

## Supported APIs and Fakes:

- AWS
    - [KMS](./amazon/kms) -> `"org.http4k:http4k-connect-amazon-kms"` / `"org.http4k:http4k-connect-amazon-kms-fake"`
    - [S3](./amazon/s3) -> `"org.http4k:http4k-connect-amazon-s3"` / `"org.http4k:http4k-connect-amazon-s3-fake"`
    - [SecretsManager](./amazon/secretsmanager) -> `"org.http4k:http4k-connect-amazon-secretsmanager"` / `"org.http4k:http4k-connect-amazon-secretsmanager-fake"`
    - [SystemsManager](./amazon/systemsmanager) -> `"org.http4k:http4k-connect-amazon-systemsmanager"` / `"org.http4k:http4k-connect-amazon-systemsmanager-fake"`
- Google
    - [Analytics](./google/analytics) -> `"org.http4k:http4k-connect-google-analytics"` / `"org.http4k:http4k-connect-google-analytic-fake"`
- [Example Template](./example) -> `"org.http4k:http4k-connect-example"` / `"org.http4k:http4k-connect-example-fake"`

## Supported Storage backends:

- [In-Memory](./core/fake) (included with all Fakes)
- [File-Based](./core/fake) (included with all Fakes)
- [JDBC](./storage/jdbc) -> `"org.http4k:http4k-connect-storage-jdbc"`
- [Redis](./storage/redis) -> `"org.http4k:http4k-connect-storage-redis"`
- [S3](./storage/s3) -> `"org.http4k:http4k-connect-storage-s3"`


## Using the Clients and the Fakes

### Client

```kotlin
Example.Http(httpHandler)
```

### Default Fake port: 22375

```
FakeExample().start()
```

## Want to add a new system or Storage backend? Read the [guide](CONTRIBUTING.md).
