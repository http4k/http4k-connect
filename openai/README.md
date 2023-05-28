# OpenAI

The http4k-connect OpenAI integration provides:
- OpenAI API client
- Plugin SDK for developing [OpenAI plugins](https://platform.openai.com/docs/plugins)
- FakeOpenAI server which can be used as testing harness for either API client or OpenAI plugins

## OpenAI API connector

The OpenAI connector provides the following Actions:

* GetModels
* ChatCompletion
* CreateEmbeddings
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

Other examples can be found [here](https://github.com/http4k/http4k-connect/tree/master/openai/fake/src/examples/kotlin).

# OpenAI Plugin SDK
<<<<<<< Updated upstream
=======

## Fake OpenAI Server

### Default Fake port: 45674
>>>>>>> Stashed changes

The OpenAPI Plugin SDK provides APIs to simply write OpenAI compliant plugins with the minimum of fuss. Simply 
use the `openAiPlugin()` function to compose your function, adding the configuration for the authorization and 
the contract endpoints which expose the API to OpenAI. 

The following plugin types are supported:
- Service - the Plugin developer provides credentials to the OpenAI UI which are used for auth. No personalisation 
of responses is possible.
- User - the OpenAI user provides credentials to the OpenAI UI which are used for auth. Prinicpals are tracked so the
API responses can be personalised.
- OAuth - users login to the Plugin application using an AuthorizationCode grant and an experience defined by the 
Plugin developer.

The SDK provides all features required by the OpenAI platform:
- A manifest endpoint for the plugin, with all of the required configuration 
- An OpenAPI specification endpoint for OpenAI to interrogate your API
- Security on the API endpoints as defined on construction. Supported auth methods are:
  - Basic Auth
  - Bearer Auth
  - OAuth (Authorization flow) - with security endpoints. 

Plugins are just `HttpHandlers` and as such can be mixed into existing applications or started alone. Example:

```kotlin
 openAiPlugin(
        info(
            apiVersion = "1.0",
            humanDescription = "addressbook" to "my great plugin",
            pluginUrl = Uri.of("http://localhost:9000"),
            contactEmail = Email.of("foo@bar"),
        ),
        UserLevelAuth(
            PluginAuthToken.Basic("realm") { it: Credentials -> it == credentials }
        ),
        Path.of("foo") / Path.of("bar") meta {
            summary = "A great api endpoint"
        } bindContract GET to
            { foo, bar ->
                { _: Request -> Response(OK).with(Body.auto<Message>().toLens() of Message("hello $foo $bar")) }
            }
    )
```

The FakeOpenAI server also provides support for running plugins locally and interacting with them as "installed" in the fake.

## Fake OpenAI Server

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

### Running plugins in the FakeOpenAI

http4k-connect OpenAI Plugins can be run locally and also "installed" into the FakeOpenAI. To install a plugin,
pass the configured `PluginIntegration` instances into the fake at construction time. The FakeOpenAI will then 
use the configuration to negotiate the connection to the plugin. Both the Fake and the Plugin should be running 
on different local ports.

```kotlin
 FakeOpenAI(
        plugins = arrayOf(
            ServicePluginIntegration(
                BearerAuth("openai api key"),
                OpenAIPluginId.of("serviceplugin"),
                Uri.of("http://localhost:10000")
            )
        )
    ).start()
```

To test the Plugin locally, start and browse to the FakeOpenAI instance. The list of installed plugins will 
be displayed and can be clicked through to an authenticated OpenAPI UI which can be used to interact with the 
exposed Plugin API.

### Default Fake port: 45674

To start:

```
FakeOpenAI().start()
```
