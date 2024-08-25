# Google Analytics GA4

### Installation

```kotlin
dependencies {
    implementation(platform("org.http4k:http4k-connect-bom:5.22.1.0"))
    implementation("org.http4k:http4k-connect-google-analytics-ga4")
    implementation("org.http4k:http4k-connect-google-analytics-ga4-fake")
}
```

The GA connector provides the following Actions:

     *  PageView
     *  Event

### Default Fake port: 35628

To start:

```
FakeGoogleAnalytics().start()
```

