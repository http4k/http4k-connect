# AzureAI

### Installation

```kotlin
dependencies {
    implementation(platform("org.http4k:http4k-connect-bom:5.20.0.0"))
    implementation("org.http4k:http4k-connect-ai-azure")
    implementation("org.http4k:http4k-connect-ai-azure-fake")
}
```

The http4k-connect AzureAI integration provides:
- AzureAI API Client
- FakeAzureAI server which can be used as testing harness for either API Client or AzureAI plugins

## AzureAI API connector

The AzureAI connector provides the following Actions:

* ChatCompletion
* CreateEmbeddings
* GenerateImage

New actions can be created easily using the same transport.

The client APIs utilise the AzureAI API Key (Bearer Auth). There is no reflection used anywhere in the library, so
this is perfect for deploying to a Serverless function.

### Example usage

```kotlin
    // create a client
val client = AzureAI.Http(AzureAIApiKey.of("foobar"),
    AzureHost.of("foobar"), Region.of("foobar"),
    http.debug())

// all operations return a Result monad of the API type
val result: Result<Sequence<CompletionResponse>, RemoteFailure> = client
    .chatCompletion(ModelName.GPT3_5, listOf(Message(User, "good afternoon")), 1000, true)

println(result.orThrow().toList())
}
```

Other examples can be found [here](https://github.com/http4k/http4k-connect/tree/master/azure/fake/src/examples/kotlin).

## Fake AzureAI Server

The Fake AzureAI provides the below actions and can be spun up as a server, meaning it is perfect for using in test
environments without using up valuable request tokens!

* GetModels
* ChatCompletion
* GenerateImage

### Security

The Fake server endpoints are secured with a BearerToken header, but the value is not checked for anything other than
presence.

### Image generation

Image generation also can be set to either URL or base-64 data return. In the case of URLs, the Fake also doubles as a
webserver for serving the images (so you can request an image and then load it from the server). Resolution PNG images
of 256x/512x/1024x are supported.

### Generation of responses

By default, a random LoremIpsum generator creates chat completion responses for the Fake. This behaviour can be
overridden to generate custom response formats (eg. structured responses) if required. To do so, create instances of
the `ChatCompletionGenerator` interface and return as appropriate.

### Default Fake port: 14504

To start:

```
FakeAzureAI().start()
```
