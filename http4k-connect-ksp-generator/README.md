# Gradle KSP Plugin

http4k-connect ships with a KSP plugin to automate the generation of the adapter extension-methods that accompany each Connect adapter. This allows you to skip creating 
those extensions manually and maintain the API of the adapter appears to contain methods for each Action.

## Generating extension methods for your adapters

1. Define your base Action (and interface) using the http4k base class and tag it with the Http4kConnectAction annotation:

```kotlin
interface APIAction<R> : Action<Result<R, RemoteFailure>>

@Http4kConnectAction
data class Reverse(val value: String) : APIAction<String> {
    override fun toRequest() = Request(POST, "/reverse").body(value)

    override fun toResult(response: Response): Result<String, RemoteFailure> =
        Success(response.bodyString())
}

```

2. Define your API adapter, tagging it with the Http4kConnectAdapter annotation:

```kotlin
@Http4kConnectAdapter
class API(rawHttp: HttpHandler) {
    private val transport = SetBaseUriFrom(Uri.of("https://api.com"))
        .then(rawHttp)

    operator fun <R> invoke(action: APIAction<R>): Result<R, RemoteFailure> =
        action.toResult(transport(action.toRequest()))
}
```

3. Install KSP into Gradle, apply it, and create a KSP configuration using the http4k-connect KSP plugin in your module:

```kotlin
plugins {
    kotlin("jvm") 
    id("com.google.devtools.ksp")
}

apply(plugin = "com.google.devtools.ksp")

dependencies {
    implementation platform("org.http4k:http4k-connect-bom:5.20.0.0")
    ksp("org.http4k:http4k-connect-ksp-generator")
}
```

4. And that's it! When Gradle runs, the following extension function will be generated:

```kotlin
fun API.reverse(value: String) = this(Reverse(value))
```

... which allows anyone to call it as if it was a standard method:

```kotlin
val api = API(JavaHttpClient())

val result: Result<String, RemoteFailure> = api.reverse("hello")
```
