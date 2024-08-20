It is very easy to implement your own clients to follow the pattern. For the system `MySystem`, you would need to:

1. Depend on the `http4k-connect-core` artifact
2. Add an Action interface and implementation:
```kotlin
interface MySystemAction<R> : Action<R>

data class Echo(val value: String) : MySystemAction<Echoed> {
    override fun toRequest() = Request(GET, "echo").body(value)
    override fun toResult(response: Response) = Echoed(response.bodyString())
}

data class Echoed(val value: String)
```
3. Add your client interface and HTTP implementation:
```kotlin
interface MySystem {
    operator fun <R : Any> invoke(action: MySystemAction<R>): R

    companion object
}

fun MySystem.Companion.Http(http: HttpHandler) = object : MySystem {
    override fun <R : Any> invoke(action: MySystemAction<R>) = action.toResult(http(action.toRequest()))
}
```

See also the [guide]() on using KSP to generate extension functions for your clients!
