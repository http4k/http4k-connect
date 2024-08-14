Each system API Adapter is modelled as a single function with arity 1 (that is it takes only a single parameter) returning a [Result4k](https://github.com/fork-handles/forkhandles/tree/trunk/result4k) Success/Failure monad type), which is known as an `Action`. The Client is responsible for managing the overall protocol with the remote system. There are also a set of extension methods generated to provide a more traditional function-based version of the same interface.

The module naming scheme for API Adapters is:

### org.http4k:http4k-connect-{vendor}-{system}

Action classes are responsible for constructing the HTTP requests and unmarshalling their responses into the http4k-connect types. There are lots of common actions built-in, but you can provide your own by simply implementing the relevant Action interface. The recommended pattern in http4k-connect is to use a Result monad type (we use Result4k) to represent the result type, but you can use anything to suit your programming model.

```kotlin
// Generic system interface
interface Example {
   operator fun <R : Any> invoke(request: ExampleAction<R>): Result<R, RemoteFailure>
}

// System-specific action
interface ExampleAction<R> : Action<Result<R, RemoteFailure>>

// Action and response classes
data class Echo(val value: String) : ExampleAction<Echoed>
data class Echoed(val value: String)

// Traditional function helpers
fun Example.echo(value: String): Result<Echoed, RemoteFailure> = this(Echo(value))
```

### Example usage
```kotlin
// constructing and using the clients
val example = Example.Http(httpHandler)

val echoed: Result<Echoed, RemoteFailure> = example.echo("hello world")
// or...
val alsoEchoed: Result<Echoed, RemoteFailure> = example(Echo("hello world"))
```
