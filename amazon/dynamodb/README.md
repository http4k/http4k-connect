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

Note that the FakeDynamo supports the majority of the Dynamo operations with the following exceptions. You can use [DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.DownloadingAndRunning.html) local instead to provide these functions:

    * BatchExecuteStatement
    * ExecuteStatement
    * ExecuteTransaction

The client APIs utilise the `http4k-aws` module for request signing, which means no dependencies on the incredibly fat Amazon-SDK JARs. This means this integration is perfect for running Serverless Lambdas where binary size is a performance factor.

### Typesafe Items & Keys
Most of the http4k-connect DynamoDb API is fairly simple, but one addition which may warrant further explanation is the http4k Lens system which is layered on top provide a typesafe API to the Item/Key objects (used for getting/setting record attributes and for defining key structures). This is useful because of the [unique way](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_AttributeValue.html) in which Dynamo handles the structure of the stored items.

- `AttributeName` - is just a data class for a named attribute in an item/key
- `AttributeValue` is the on-the-wire format of an attribute with it's requisite type. Examples of this are: `{ "S": "hello" }` or `{ "BOOL": true }` or `{ "NS": ["123"] }`. Construction of these AttributeValues can be done using factory functions such as `AttributeValue.Str("string")`. `AttributeValues` can be primitives (BOOL, S, N), Sets (NS, BS), or collections of other AttributeValues (L, M).
- `Item` and `Key` are just typealiases for `Map<AttributeName, AttributeValue>`. They have convenience construction methods `Item()` and `Key()`. These are sent to and returned in the messages between a client and DynamoDb.

When constructing `Actions` or deconstructing their responses for Items/Keys, we can populate or interrogate the Map returned manually, but we may be unsure of the types. To that end, the http4k Lens system has been used to create a typesafe binding between the names and types of the AttributeValues. This system supports all of [types](https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_AttributeValue.html) available in the Dynamo type system, and also provides mapping for both common JDK types (including popular Java Datetime types) and required/optional attributes (ie. `String` vs `String?`).

### Typesafe lens-based Dynamo Object Mapper
Using the lens system and http4k automapping facilities, http4k-connect also supports dynamic flattening of objects into the DynamoDB schema with zero boilerplate. Simply create a lens and apply it to your object to inject values into the DynamoDB Item. the structure of maps and lists are preserved by collapsing them into a single DynamoDB field. This is implemented as a standard extension function on any of the http4k automarshalling object mappers (Jackson, Moshi, GSON etc..):
```
data class AnObject(val str: String, val num: Int)

val input = AnObject("foobar", 123)

val lens = Moshi.autoDynamoLens<AnObject>()

val item: Item = Item().with(lens of input)

val extracted: AnObject = lens(item)
```

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

#### Null handling and sparse indexes

The default mapping for null values of manually mapped optional attributes in DynamoDB will assign them to an explicit 
null attribute:
```kotlin
val attrS = Attribute.string().optional("optS")
val item = Item(attrS of null)

// item now contains "optS": { "NULL": true }
```

When utilizing an optional attribute as a key in a secondary index (creating a sparse index), the attribute must be 
absent rather than null. To achieve this, set `ignoreNull` to true in the attribute definition.
```kotlin
val attrS = Attribute.string().optional("optS", ignoreNull = true)
```

When incorporating this attribute into the secondary index schema, it is necessary to convert it into a mandatory 
(non-optional) attribute.
```kotlin
// attrS is of type Attribute<String?>

attrS.asRequired() // will be of type Attribute<String>
```

Note: null properties of automapped objects (using `autoDynamoLens()`) will be ignored by default.

### DynamoDB Table Repository

A simplified API for mapping documents to and from a single table with `get`, `put`, `scan`, `query`, etc.

```kotlin
private const val USE_REAL_CLIENT = false

// define our data class
private data class Person(
    val name: String,
    val id: UUID = UUID.randomUUID()
)

private val john = Person("John")
private val jane = Person("Jane")

fun main() {
    // build client (real or fake)
    val http = if (USE_REAL_CLIENT) JavaHttpClient() else FakeDynamoDb()
    val dynamoDb = DynamoDb.Http(Region.CA_CENTRAL_1, { AwsCredentials("id", "secret") }, http.debug())

    // defined table mapper
    val table = dynamoDb.tableMapper<Person, UUID, Unit>(
        tableName = TableName.of("people"),
        hashKeyAttribute = Attribute.uuid().required("id")
    )

    // create table
    table.createTable()

    // save
    table.save(john)
    table.save(jane)

    // get
    val johnAgain = table.get(john.id)

    // scan
    val people = table.primaryIndex().scan().take(10)

    // delete
    table.delete(john)
}
```

See another [example](/amazon/dynamodb/client/src/examples/kotlin/using_the_table_mapper.kt) with secondary indices.

Complex scan or query expressions may be constructed using functions from the `KeyConditionBuilder` and `FilterExpressionBuilder` classes
(which therefore provide a scan/query DSL). This DSL is not complete, however it should cover most of the common use cases. 

Examples:
```kotlin
val idAttr = Attribute.uuid().required("id")
val nameAttr = Attribute.string().required("name")

// scan with filter
val people = table.primaryIndex().scan {
    filterExpression {
        (nameAttr beginsWith "J") and not(nameAttr eq "Jimmy")
    }
}

// query with key condition (doesn't actually make much sense is this example)
val anotherJohn = table.primaryIndex().query {
    keyCondition {
        hashKey eq john.id
    }
}
```

General query pattern with combined key condition and filter expression
```kotlin
table.primaryIndex().query {
    keyCondition {
        (hashKey eq hashValue) and (sortKey gt sortValue)
    }
    filterExpression {
        (fooAttr ne "foo") or (barAttr isIn listOf(5, 6, 7)) and (bazAttr lt quzAttr)
    }
}
```

Notes:
 - `hashKey` and `sortKey` are special identifiers to be used in the `keyCondition` that represent the actual key 
   attributes of the current index
 - the hash key condition must use the `eq` operator (no other operators allowed),  
 - in the sort key condition the following operators are supported: `eq` `gt`,`ge`,`lt`, `le` (for `=`, `>`, `>=`, `<`, `<=`), 
   `beginsWith`, and the `sortKey.between(val1, val2)` function
 - in the filter expression concrete attributes must be used instead of `hashKey` or `sortKey`
 - the filter expression supports all of the above operators plus `ne` (`<>`), `isIn`, `contains`, `attributeExists(attr)`,
   and `attributeNotExists(attr)`
 - in a filter expression the first operand in a comparison must be an attribute, the second operand is either a value
   or another attribute of the same type (so `xAttr eq 42` and `xAttr ne yAttr` are supported, but `42 eq xAttr` is not) 
 - the logical operators `and` and `or` in this DSL are always evaluated from left to right (i.e. there is no higher precedence for `and`),
   you should use parenthesis to change the order of evaluation 
 - if an operand of a logical operator is `null` it will simply be omitted. This allows building queries with optional conditions:
```kotlin
filterExpression {
   val nameFilter = name?.let { nameAttr eq it }
   val sizeFilter = size?.let { sizeAttr eq it }
   
   // results in either a filter for name, a filter for size, a filter for both, or in no filter at all 
   nameFilter and sizeFilter 
}
``` 

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
val str: String? = attrS(item)

// all operations return a Result monad of the API type
val deleteResult: Result<TableDescriptionResponse, RemoteFailure> = client.deleteTable(table)
println(deleteResult)
```
