<hr/>
<img src="logo.png" alt="http4k-connect"/>
<hr/>

![https://bintray.com/http4k/maven/http4k-connect-bom/_latestVersion](https://api.bintray.com/packages/http4k/maven/http4k-connect-bom/images/download.svg)
![.github/workflows/build.yaml](https://github.com/http4k/http4k-connect/workflows/.github/workflows/build.yaml/badge.svg)
![.github/workflows/release.yaml](https://github.com/http4k/http4k-connect/workflows/.github/workflows/release.yaml/badge.svg)
![http://kotlinlang.org](https://img.shields.io/badge/kotlin-1.4-blue.svg)

http4k-connect is a set of API libraries for connecting to popular third-party services using http4k compatible APIs, along with Fake implementations for usage during testing and Storage implementations. These all utilise the uniform `Server as a Function` 
`HttpHandler` interface exposed by [http4k](https://http4k.org), so you can plug everything together completely in-memory.

To install:

```groovy
dependencies {
    // install the plaform...
    implementation platform("org.http4k:http4k-connect-bom:0.19.0.0")

    // ...then choose a client
    implementation "org.http4k:http4k-connect-amazon-s3"

    // ...a fake for testing
    testImplementation "org.http4k:http4k-connect-amazon-s3-fake"

    // ...and a storage backend (optional)
    testImplementation "org.http4k:http4k-storage-redis"
}
```

Supported APIs and Fakes:

- AWS
    - [S3](./amazon/s3) -> `"org.http4k:http4k-connect-amazon-s3"` / `"org.http4k:http4k-connect--amazon-s3-fake"`
    - [SecretsManager](./amazon/secretsmanager) -> `"org.http4k:http4k-connect-amazon-secretsmanager"` / `"org.http4k:http4k-connect--amazon-secretsmanager-fake"`
- Google
    - [Analytics](./google/analytics) -> `"org.http4k:http4k-connect-google-analytics"` / `"org.http4k:http4k-connect-google-analytic-fake"`
- [Example Template](./example) -> `"org.http4k:http4k-connect-example"` / `"org.http4k:http4k-connect-example-fake"`

Supported Storage backends:

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
