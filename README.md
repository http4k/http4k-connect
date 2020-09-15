# http4k-connect

![.github/workflows/build.yaml](https://github.com/http4k/http4k-connect/workflows/.github/workflows/build.yaml/badge.svg)
![.github/workflows/release.yaml](https://github.com/http4k/http4k-connect/workflows/.github/workflows/release.yaml/badge.svg)

http4k Connector libraries for external APIs using http4k compatible APIs, along with Fake implementations for usage during testing.

To install:

```groovy
dependencies {
    implementation platform("org.http4k:http4k-connect-bom:0.2.0.0")
    implementation "org.http4k:http4k-connect-google-analytics"
    implementation "org.http4k:http4k-connect-google-analytics-fake"
}
```

Currently supported APIs:

- Google Analytics - default port: 30000
