# DynamoDb

The DynamoDb connector provides the following Actions:

    * CreateTable
    * DeleteTable
    * DescribeTable
    * ListTables
    * UpdateTable

    * DeleteItem
    * GetItem
    * PutItem
    * Query
    * Scan
    * UpdateItem

    * TransactGetItems
    * TransactWriteItems

    * BatchGetItem
    * BatchWriteItem

    * ExecuteTransaction
    * ExecuteStatement
    * BatchExecuteStatement

Note that there currently is no Fake implementation of the Dynamo adapter. You can use [DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html) local instead.

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a performance factor.

### Typesafe Items & Keys
Most of the http4k-connect DynamoDb API is fairly simple, but one addition which may warrant further explanation is the http4k Lens system which is layered on top provide a typesafe API to the Item/Key objects (used for getting/setting record attributes and for defining key structures). This is useful because of the [unique way](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_AttributeValue.html) in which Dynamo handles the structure of the stored items.

- `AttributeName` - is just a data class for a named attribute in an item/key
- `AttributeValue` is the on-the-wire format of an attribute with it's requisite type. Examples of this are: `{ "S": "hello" }` or `{ "BOOL": true }` or `{ "NS": ["123"] }`. Construction of these AttributeValues can be done using factory functions such as `AttributeValue.Str("string")`. `AttributeValues` can be primitives (BOOL, S, N), Sets (NS, BS), or collections of other AttributeValues (L, M).
- `Item` and `Key` are just typealiases for `Map<AttributeName, AttributeValue>`. They have convenience construction methods `Item()` and `Key()`. These are sent to and returned in the messages between a client and DynamoDb.

When constructing `Actions` or deconstructing their responses for Items/Keys, we can populate or interrogate the Map returned manually, but we may be unsure of the types. To that end, the http4k Lens system has been used to create a typesafe binding between the names and types of the AttributeValues. This system supports all of [types](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_AttributeValue.html) available in the Dynamo type system, and also provides mapping for both common JDK types (including popular Java Datetime types) and required/optional attributes (ie. `String` vs `String?`).

#### Example
Given that a record in Dynamo will have many typed values, we first define a set of attributes which are relevant for the case in question. These methods construct Lenses which can be used to inject or extract typed values safely:
```kotlin
val attrS = Attribute.string().optional("theNull")
val attrBool = Attribute.boolean().required("theBool")
val attrN = Attribute.int().optional("theNum")
val attrI = Attribute.instant().required("theInstant")
val attrM = Attribute.map().required("theMap")
```

To construct an Item or Key to send to Dynamo, we can bind the values at the same time:
```kotlin
val item = Item(
    attrS of "hello",
    attrN of null,
    attrM of Item(attrI of Instant.now())
)
```

To deconstruct an Item or Key to send to Dynamo, we simply apply the attributes as functions to the container:
```kotlin
val string: String? = attrS(item)
val boolean: Boolean = attrBool(item)
val instant: Instant = attrI(attrM(item))
```

On missing or invalid value, an exception is thrown. To counter this we can use the built in Result4k monad marshalling: 
```kotlin
val boolean: Result<Boolean, LensFailure> = attrBool.asResult()(item)
```

It is also possible to `map()` lenses to provide marshalling into your own types.

### General example usage of API client

```kotlin
    // we can connect to the real service
val http: HttpHandler = JavaHttpClient()

// create a client
val client = DynamoDb.Http(Region.of("us-east-1"), { AwsCredentials("accessKeyId", "secretKey") }, http.debug())

val table = TableName.of("myTable")

// we can bind values to the attributes
client.putItem(
    table,
    Item = mapOf(
        attrS to "foobar",
        attrBool to true,
        attrB to Base64Blob.encode("foo"),
        attrBS to setOf(Base64Blob.encode("bar")),
        attrN to 123,
        attrNS to setOf(123, 12.34),
        attrL to listOf(
            List(listOf(AttributeValue.Str("foo"))),
            Num(123),
            Null()
        ),
        attrM to mapOf(attrS to "foo", attrBool to false),
        attrSS to setOf("345", "567"),
        attrNL to null
    )
)

// lookup an item from the database
val item = client.getItem(table, key = mapOf(attrS to "hello")).valueOrNull()!!.item!!
val str: String? = attrS[item]

// all operations return a Result monad of the API type
val deleteResult: Result<TableDescriptionResponse, RemoteFailure> = client.deleteTable(table)
println(deleteResult)
```
