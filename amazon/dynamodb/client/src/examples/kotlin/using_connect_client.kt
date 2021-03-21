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
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.TableName
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.filter.debug

private val attrBool = Attribute.boolean("theBool")
private val attrB = Attribute.base64Blob("theBase64Blob")
private val attrBS = Attribute.base64Blobs("theBase64Blobs")
private val attrN = Attribute.number("theNum")
private val attrNS = Attribute.numbers("theNums")
private val attrL = Attribute.list("theList")
private val attrM = Attribute.map("theMap")
private val attrS = Attribute.string("theString")
private val attrSS = Attribute.strings("theStrings")
private val attrNL = Attribute.string("theNull")

fun main() {
    // we can connect to the real service
    val http: HttpHandler = JavaHttpClient()

    // create a client
    val client = DynamoDb.Http(Region.of("us-east-1"), { AwsCredentials("accessKeyId", "secretKey") }, http.debug())

    val table = TableName.of("myTable")

    // we can bind values to the attributes
    client.putItem(
        table,
        item = mapOf(
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
    val item = client.getItem(table, key = mapOf(attrS to "hello")).successValue().item
    val str: String? = attrS[item]

    // all operations return a Result monad of the API type
    val deleteResult: Result<TableDescriptionResponse, RemoteFailure> = client.deleteTable(table)
    println(deleteResult)
}

