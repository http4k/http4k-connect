# Storage Core

### Installation 

```kotlin
dependencies {
    implementation(platform("org.http4k:http4k-connect-bom:5.22.1.0"))
    implementation("org.http4k:http4k-connect-storage-core")
}
```

http4k-connect contains a simple lightweight pluggable Key-Value storage abstraction in the `http4k-connect-storage-core` module, which can be used to serialise objects to an underlying store.

Standard Operations are:

- Set
- Get
- Remove
- Get all keys with a particular prefix
- Remove all keys with a particular prefix

### In-Memory Storage

All data is held in process memory.

```kotlin

data class AnEntity(val name: String)

val storage = Storage.InMemory()
storage["myKey"] = AnEntity("hello")

println(storage["myKey"])

storage.removeAll("myKey")
```


### On-Disk Storage

All data is serialised to disk by passing it though an http4k AutoMarshalling adapter (see the `http4k-format-XXX` modules). In the example below we use a JSON adapter backed by Moshi (which is the default).

```kotlin
import java.io.File

data class AnEntity(val name: String)

val storage = Storage.Disk(File("."), Moshi)

storage["myKey"] = AnEntity("hello")

println(storage["myKey"])

storage.removeAll("myKey")
```

