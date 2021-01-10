# Example Service

The Example connector provides the following Actions:

     *  Echo

### Example usage

```kotlin
const val USE_REAL_CLIENT = false

fun main() {
    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeExample()

    // create a client
    val example = Example.Http(http.debug())

    // all operations return a Result monad of the API type
    val echoedResult: Result<Echoed, RemoteFailure> = example.echo("hello")
    println(echoedResult)
}
```

### Default Fake port: 22375

To start:

```
FakeExample().start()
```
