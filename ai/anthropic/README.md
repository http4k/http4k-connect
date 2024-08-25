# AnthropicAI

### Installation

```kotlin
dependencies {
    implementation(platform("org.http4k:http4k-connect-bom:5.22.1.0"))
    implementation("org.http4k:http4k-connect-ai-anthropic")
    implementation("org.http4k:http4k-connect-ai-anthropic-fake")
}
```

The http4k-connect AnthropicAI integration provides:

- AnthropicAI API Client
- FakeAnthropicAI server which can be used as testing harness for the API Client 

## AnthropicAI API connector

The AnthropicAI connector provides the following Actions:

- MessageCompletion (streaming and non-streaming)

* New actions can be created easily using the same transport.

The client APIs utilise the AnthropicAI API Key. There is no reflection used anywhere in the library, so
this is perfect for deploying to a Serverless function.

## Fake AnthropicAI Server

The Fake AnthropicAI provides the below actions and can be spun up as a server, meaning it is perfect for using in test
environments without using up valuable request tokens!

- MessageCompletion (streaming and non-streaming)

### Security

The Fake server endpoints are secured with a API key header, but the value is not checked for anything other than presence.

### Generation of responses

By default, a random LoremIpsum generator creates message completion responses for the Fake. This behaviour can be
overridden to generate custom response formats (eg. structured responses) if required. To do so, create instances of
the `MessageCompletionGenerator` interface and return as appropriate.

### Default Fake port: 18909

To start:

```
FakeAnthropicAI().start()
```
