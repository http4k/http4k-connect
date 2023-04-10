# OpenAI

The OpenAI connector provides the following Actions:

* GetModels
* ChatCompletion

The client APIs utilise the an OpenAI API Key (Bearer Auth). There is no reflection used anywhere in the

### Example usage

```kotlin
const val USE_REAL_CLIENT = false

fun main() {
    // we can connect to the real service or the fake (drop in replacement)
    val http: HttpHandler = if (USE_REAL_CLIENT) JavaHttpClient() else FakeOpenAI()

    // create a client
    val client = OpenAI.Http(OpenAIToken.of("foobar"), http.debug())

    // all operations return a Result monad of the API type
    val result: Result<Models, RemoteFailure> = client
        .getModels()

    println(result)
}
```

### Default Fake port: 45674

The Fake OpenAI provides the above actions and can be spun up as a server, meaning it is perfect for using in test
environments without using up valuable request tokens!

To start:

```
FakeOpenAI().start()
```
