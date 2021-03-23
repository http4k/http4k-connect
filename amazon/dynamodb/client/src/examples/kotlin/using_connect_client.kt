import dev.forkhandles.result4k.Result
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.Http
import org.http4k.connect.amazon.dynamodb.action.AttributeValue
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.List
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.Null
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.Num
import org.http4k.connect.amazon.dynamodb.action.TableDescriptionResponse
import org.http4k.connect.amazon.dynamodb.deleteTable
import org.http4k.connect.amazon.dynamodb.getItem
import org.http4k.connect.amazon.dynamodb.putItem
import org.http4k.connect.amazon.model.Attribute
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.Item
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.TableName
import org.http4k.connect.amazon.model.with
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.filter.debug

private val attrBool = Attribute.boolean().required("theBool")
private val attrB = Attribute.base64Blob().required("theBase64Blob")
private val attrBS = Attribute.base64Blobs().required("theBase64Blobs")
private val attrN = Attribute.int().required("theNum")
private val attrNS = Attribute.ints().required("theNums")
private val attrL = Attribute.list().required("theList")
private val attrM = Attribute.map().required("theMap")
private val attrS = Attribute.string().required("theString")
private val attrSS = Attribute.strings().required("theStrings")
private val attrNL = Attribute.string().optional("theNull")

fun main() {
    // we can connect to the real service
    val http: HttpHandler = JavaHttpClient()

    // create a client
    val client = DynamoDb.Http(Region.of("us-east-1"), { AwsCredentials("accessKeyId", "secretKey") }, http.debug())

    val table = TableName.of("myTable")

    // we can bind typed values to the attributes of an item
    client.putItem(
        table,
        item = Item(
            attrS of "foobar",
            attrBool of true,
            attrB of Base64Blob.encode("foo"),
            attrBS of setOf(Base64Blob.encode("bar")),
            attrN of 123,
            attrNS of setOf(123, 321),
            attrL of listOf(
                List(listOf(AttributeValue.Str("foo"))),
                Num(123),
                Null()
            ),
            attrM of Item().with(attrS of "foo", attrBool of false),
            attrSS of setOf("345", "567"),
            attrNL of null
        )
    )

    // lookup an item from the database
    val item = client.getItem(table, key = Item(attrS of "hello")).successValue().item!!
    val str: String = attrS(item)

    // all operations return a Result monad of the API type
    val deleteResult: Result<TableDescriptionResponse, RemoteFailure> = client.deleteTable(table)
    println(deleteResult)
}

