# Mattermost Service

```kotlin
dependencies {
    implementation(platform("org.http4k:http4k-connect-bom:5.22.1.0"))
    implementation("org.http4k:http4k-connect-mattermost-rest")
}
```

The Mattermost connector provides the following Actions:

- TriggerWebhook

### Example usage

```kotlin
const val USE_REAL_CLIENT = false

fun main() {
    val payloads = Storage.InMemory<List<TriggerWebhookPayload>>()

    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeMattermost(payloads)

    // create a client
    val mattermost = Mattermost.Http(
        baseUri = Uri.of("https://mattermost.com"),
        http = http.debug()
    )

    val payload = TriggerWebhookPayload(
        text = "Hello world",
        iconUrl = "http://icon.url",
    )

    // all operations return a Result monad of the API type
    val result: Result<String, RemoteFailure> = mattermost.triggerWebhook(
        key = UUID.randomUUID().toString(),
        payload = payload,
    )
    println(result)

    println(payloads)
}
```

### Default Fake port: 54786

To start:

```kotlin
FakeMattermost().start()
```
