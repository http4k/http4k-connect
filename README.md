<hr/>
<img src="logo.png" alt="http4k-connect"/>
<hr/>

![https://bintray.com/http4k/maven/http4k-connect-bom/_latestVersion](https://api.bintray.com/packages/http4k/maven/http4k-connect-bom/images/download.svg)
![.github/workflows/build.yaml](https://github.com/http4k/http4k-connect/workflows/.github/workflows/build.yaml/badge.svg)
![.github/workflows/release.yaml](https://github.com/http4k/http4k-connect/workflows/.github/workflows/release.yaml/badge.svg)
![http://kotlinlang.org](https://img.shields.io/badge/kotlin-1.4-blue.svg)

http4k-connect is a set of lightweight API libraries for connecting to popular third-party cloud services using [http4k](https://http4k.org) compatible APIs, along with Fake implementations for usage during local testing. These are all underpinned by a variation on the uniform [Server as a Function](https://monkey.org/~marius/funsrv.pdf) model powered by the `HttpHandler` interface exposed by [http4k](https://http4k.org), so you can:
 
1. Take advantage of the simple and powerful SaaF model and APIs used in http4k.
1. Plug everything together completely in-memory and take advantage of this powerful model.
1. Have access to the underlying HTTP clients (and hence add metrics or logging).
1. Run stateful Fake implementations of 3rd party systems locally or in test environments.

Although centered around usage in http4k-based projects, http4k-connect does not require this and the libraries are usable from any JVM application.

## Rationale
Although convenient, many client libraries introduce many heavyweight dependencies or contain a plethora of non-required functionality, which can have a large effect on binary size. As an alternative, http4k-connect provides lightweight versions of popular APIs covering standard use-cases.

## Concepts

### System Client Modules (named: http4k-{vendor}-{system})
Each system client is modelled as a single function with arity 1 (that is it takes only a single parameter) returning a [Result4k](https://github.com/fork-handles/forkhandles/tree/trunk/result4k) Success/Failure monad type), which is known as an `Action`. The Client is responsible for managing the overall protocol with the remote system. There are also a set of extension methods generated to provide a more traditional function-based version of the same interface.

Action classes are responsible for constructing the HTTP requests and unmarshalling their responses into the http4k-connect types. There are lots of common actions built-in, but you can provide your own by simply implementing the relevant Action interface.

```kotlin
// Generic system interface
interface Example {
   operator fun <R : Any> invoke(request: ExampleAction<R>): Result<R, RemoteFailure>
}

// System-specific action
interface ExampleAction<R> : Action<R>

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

val echoed: Result<Echoed, RemoteFailure> = example(Echo("hello world"))
// or...
val alsoEchoed: Result<Echoed, RemoteFailure> = example.echo("hello world")
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

## Installation
```groovy
dependencies {
    // install the platform...
    implementation platform("org.http4k:http4k-connect-bom:2.7.0.0")

    // ...then choose a client
    implementation "org.http4k:http4k-connect-amazon-s3"

    // ...a fake for testing
    testImplementation "org.http4k:http4k-connect-amazon-s3-fake"

    // ...and a storage backend (optional)
    testImplementation "org.http4k:http4k-storage-redis"
}
```

## Supported APIs and Fakes:

- AWS
    - [KMS](./amazon/kms) -> `"org.http4k:http4k-connect-amazon-kms"` / `"org.http4k:http4k-connect-amazon-kms-fake"`
    - [Lambda](./amazon/lambda) -> `"org.http4k:http4k-connect-amazon-lambda"` / `"org.http4k:http4k-connect-amazon-lambda-fake"`
    - [S3](./amazon/s3) -> `"org.http4k:http4k-connect-amazon-s3"` / `"org.http4k:http4k-connect-amazon-s3-fake"`
    - [STS](./amazon/sts) -> `"org.http4k:http4k-connect-amazon-sts"` / `"org.http4k:http4k-connect-amazon-sts-fake"`
    - [SecretsManager](./amazon/secretsmanager) -> `"org.http4k:http4k-connect-amazon-secretsmanager"` / `"org.http4k:http4k-connect-amazon-secretsmanager-fake"`
    - [SystemsManager](./amazon/systemsmanager) -> `"org.http4k:http4k-connect-amazon-systemsmanager"` / `"org.http4k:http4k-connect-amazon-systemsmanager-fake"`
- Google
    - [Analytics](./google/analytics) -> `"org.http4k:http4k-connect-google-analytics"` / `"org.http4k:http4k-connect-google-analytic-fake"`
- [Example Template](./example) -> `"org.http4k:http4k-connect-example"` / `"org.http4k:http4k-connect-example-fake"`

## Supported Storage backends (named http4k-connect-storage-{technology}>)

- [In-Memory](./core/fake) (included with all Fakes)
- [File-Based](./core/fake) (included with all Fakes)
- [JDBC](./storage/jdbc) -> `org.http4k:http4k-connect-storage-jdbc`
- [Redis](./storage/redis) -> `org.http4k:http4k-connect-storage-redis`
- [S3](./storage/s3) -> `org.http4k:http4k-connect-storage-s3"`

## Want to add a new system or Storage backend? Read the [guide](CONTRIBUTING.md).
