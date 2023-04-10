# OpenAI

The OpenAI connector provides the following Actions:

* GetModels
* ChatCompletion
* GenerateImage

New actions can be created easily using the same transport.

The client APIs utilise the OpenAI API Key (Bearer Auth). There is no reflection used anywhere in the library, so
this is perfect for deploying to a Serverless function.

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

To start:

```
FakeOpenAI().start()
```

The Fake OpenAI provides the below actions and can be spun up as a server, meaning it is perfect for using in test
environments without using up valuable request tokens!

* GetModels
* ChatCompletion
* GenerateImage

### Security

The Fake server endpoints are secured with a BearerToken header, but the value is not checked for anything other than
presence.

### Streaming completions

Currently streaming of responses is not supported by the Fake.

### Image generation

Image generation also can be set to either URL or base-64 data return. In the case of URLs, the Fake also doubles as a
webserver for serving the images (so you can request an image and then load it from the server). Resolution PNG images
of 256x/512x/1024x are supported.

### Generation of responses

By default, a random LoremIpsum generator creates chat completion responses for the Fake. This behaviour can be
overridden to generate custom response formats (eg. structured responses) if required. To do so, create instances of
the `ChatCompletionGenerator` interface and return as appropriate.
