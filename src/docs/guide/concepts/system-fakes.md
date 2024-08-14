Each module comes with it's own Fake system which implements the remote HTTP interface. In like with the `Server as a Function` concept, this Fake class implements `HttpHandler` and:

1. Can be used in in-memory tests as a swap-out replacement for an HTTP client
2. Can be started and bound to a HTTP port - each Fake has it's own unique port
3. Can be deployed into test environments as a replacement for the real thing.
4. Can be used to simulate Chaotic behaviour using the built in OpenApi interface (see http://localhost:<port>/chaos)

The module naming scheme for Fakes is:

### org.http4k:http4k-connect-{vendor}-{system}-fake

Inject the fake in place of a standard HTTP Handler, or start the Fake as a server with:
```
FakeExample().start()
> Started FakeExample on 22375
```
