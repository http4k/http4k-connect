## Rationale
Although convenient, many client libraries introduce many heavyweight dependencies or contain a plethora of non-required functionality, which can have a large effect on binary size. As an alternative, http4k-connect provides lightweight versions of popular APIs covering standard use-cases.


### System Client Modules (named: http4k-{vendor}-{system})
Each system client is modelled as a single function with arity 1 (that is it takes only a single parameter) returning a [Result4k](https://github.com/fork-handles/forkhandles/tree/trunk/result4k) Success/Failure monad type), which is known as an `Action`. The Client is responsible for managing the overall protocol with the remote system. There are also a set of extension methods generated to provide a more traditional function-based version of the same interface.

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

### System Fake Modules (named http4k-{vendor}-{system}-fake)
Each module comes with it's own Fake system which implements the remote HTTP interface. In like with the `Server as a Function` concept, this Fake class implements `HttpHandler` and:

1. Can be used in in-memory tests as a swap-out replacement for an HTTP client
2. Can be started and bound to a HTTP port - each Fake has it's own unique port
3. Can be deployed into test environments as a replacement for the real thing.
4. Can be used to simulate Chaotic behaviour using the built in OpenApi interface (see http://localhost:<port>/chaos)

Start the Fake with:
```
FakeExample().start()
> Started FakeExample on 22375
```
