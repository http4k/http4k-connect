# http4k-connect

![.github/workflows/build.yaml](https://github.com/http4k/http4k-connect/workflows/.github/workflows/build.yaml/badge.svg)
![.github/workflows/release.yaml](https://github.com/http4k/http4k-connect/workflows/.github/workflows/release.yaml/badge.svg)
<a href="http://kotlinlang.org"><img alt="kotlin" src="https://img.shields.io/badge/kotlin-1.4-blue.svg"></a>

http4k Connector libraries for external APIs using http4k compatible APIs, along with Fake implementations for usage during testing.

To install:

```groovy
dependencies {
    implementation platform("org.http4k:http4k-connect-bom:0.8.0.0")
    implementation "org.http4k:http4k-connect-google-analytics"
    implementation "org.http4k:http4k-connect-google-analytics-fake"
}
```

Currently supported APIs and Fakes:

- Amazon S3
    - Global: default port: 26467
    - Bucket: default port: 42628
- Google Analytics - default port: 35628
