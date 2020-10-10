# http4k-connect

![.github/workflows/build.yaml](https://github.com/http4k/http4k-connect/workflows/.github/workflows/build.yaml/badge.svg)
![.github/workflows/release.yaml](https://github.com/http4k/http4k-connect/workflows/.github/workflows/release.yaml/badge.svg)
<a href="http://kotlinlang.org"><img alt="kotlin" src="https://img.shields.io/badge/kotlin-1.4-blue.svg"></a>

http4k Connector libraries for external APIs using http4k compatible APIs, along with Fake implementations for usage during testing. These all utilise the uniform `Server as a Function` 
`HttpHandler` interface exposed by [http4k](https://http4k.org), so you can plug everything together completely in-memory.

To install:

```groovy
dependencies {
    implementation platform("org.http4k:http4k-connect-bom:0.12.0.0")
    implementation "org.http4k:http4k-connect-amazon-s3"
    implementation "org.http4k:http4k-connect-amazon-s3-fake"
}
```

Currently supported APIs and Fakes:

- [Amazon S3](./amazon/s3)
- [Google Analytics](./google/analytics)
- [Example Template](./example)


## Using the Clients and the Fakes

### Client

```kotlin
Example.Companion.Http(...)
```

### Default Fake port: 22375

```
FakeExample().start()
```

## Want to add a new system? Read the [guide](CONTRIBUTING.md).
