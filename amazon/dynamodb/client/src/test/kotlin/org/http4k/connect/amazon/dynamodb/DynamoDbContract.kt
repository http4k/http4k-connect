package org.http4k.connect.amazon.dynamodb

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasElement
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.dynamodb.action.AttributeDefinition
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.Bool
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.List
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.Null
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.Num
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.Str
import org.http4k.connect.amazon.dynamodb.action.BillingMode.PAY_PER_REQUEST
import org.http4k.connect.amazon.dynamodb.action.BillingMode.PROVISIONED
import org.http4k.connect.amazon.dynamodb.action.DynamoDataType.S
import org.http4k.connect.amazon.dynamodb.action.Item
import org.http4k.connect.amazon.dynamodb.action.KeySchema
import org.http4k.connect.amazon.dynamodb.action.KeyType.HASH
import org.http4k.connect.amazon.dynamodb.action.ProvisionedThroughput
import org.http4k.connect.amazon.dynamodb.action.ReqGetItem
import org.http4k.connect.amazon.dynamodb.action.ReqStatement
import org.http4k.connect.amazon.dynamodb.action.ReqWriteItem
import org.http4k.connect.amazon.dynamodb.action.TransactGetItem.Companion.Get
import org.http4k.connect.amazon.dynamodb.action.TransactWriteItem.Companion.Delete
import org.http4k.connect.amazon.dynamodb.action.TransactWriteItem.Companion.Put
import org.http4k.connect.amazon.dynamodb.action.TransactWriteItem.Companion.Update
import org.http4k.connect.amazon.model.Attribute
import org.http4k.connect.amazon.model.AttributeName
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.TableName
import org.http4k.connect.amazon.model.name
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.UUID

abstract class DynamoDbContract(
    http: HttpHandler,
    private val duration: Duration = Duration.ofSeconds(10)
) : AwsContract(http) {

    private val dynamo by lazy {
        DynamoDb.Http(aws.region, { aws.credentials }, http)
    }

    private val table = TableName.of("http4k-connect" + UUID.randomUUID().toString())

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
    private val attrMissing = Attribute.string().optional("theMissing")

    @BeforeEach
    fun create() {
        assertThat(dynamo.createTable(table, attrS.name).TableDescription.ItemCount, equalTo(0))
        waitForUpdate()
    }

    @AfterEach
    fun after() {
        dynamo.deleteTable(table)
    }

    @Test
    fun `transactional items`() {
        with(dynamo) {
            transactWriteItems(
                listOf(
                    Update(
                        table,
                        Item(attrS of "hello"),
                        "SET ${attrBool.name} = :c",
                        ExpressionAttributeValues = mapOf(":c" to Bool(true))
                    ),
                    Put(table, item("hello2")),
                    Put(table, item("hello3")),
                    Delete(table, Item(attrS of "hello4"))
                )
            ).successValue()

            val result = transactGetItems(
                listOf(
                    Get(table, Item(attrS of "hello2")),
                    Get(table, Item(attrS of "hello3"))
                )
            ).successValue()

            assertThat(attrS(result.responses[0]), equalTo("hello2"))
            assertThat(attrS(result.responses[1]), equalTo("hello3"))
        }
    }

    @Test
    fun `batch operations`() {
        with(dynamo) {
            val write = batchWriteItem(
                mapOf(
                    table to listOf(
                        ReqWriteItem.Put(item("hello2")),
                        ReqWriteItem.Delete(Item(attrS of "hello"))
                    )
                )
            ).successValue()

            assertThat(write.UnprocessedKeys, absent())

            val get = batchGetItem(
                mapOf(table to ReqGetItem.Get(listOf(Item(attrS of "hello2"))))
            ).successValue()

            assertThat(get.UnprocessedItems, absent())
        }
    }

    @Test
    fun `partiSQL operations`() {
        with(dynamo) {
            putItem(table, item("hello")).successValue()

            executeStatement(statement()).successValue()

            batchExecuteStatement(listOf(ReqStatement(statement()))).successValue()

//            executeTransaction(listOf(ParameterizedStatement(delete()))).successValue()
        }
    }

    @Test
    fun `item lifecycle`() {
        with(dynamo) {
            putItem(table, item("hello")).successValue()

            assertThat(getItem(table, Item(attrS of "hello4")).successValue().item, absent())

            val item = getItem(table, Item(attrS of "hello")).successValue().item!!

            assertThat(attrS(item), equalTo("hello"))
            assertThat(attrBool(item), equalTo(true))
            assertThat(attrB(item), equalTo(Base64Blob.encode("foo")))
            assertThat(attrBS(item), equalTo(setOf(Base64Blob.encode("bar"))))
            assertThat(attrN(item), equalTo(123))
            assertThat(attrNS(item), equalTo(setOf(123, 321)))
            assertThat(attrL(item), equalTo(listOf(List(listOf(Str("foo"))), Num(123), Null())))
            assertThat(attrSS(item), equalTo(setOf("345", "567")))
            assertThat(attrMissing(item), absent())
            assertThat(attrNL(item), absent())
            assertThat(attrM(item), equalTo(Item(attrS of "foo", attrBool of false)))

            updateItem(
                table,
                Item(attrS of "hello"),
                null,
                "set ${attrN.name} = :val1",
                expressionAttributeValues = mapOf(":val1" to Num(321))
            ).successValue()

            val updatedItem = getItem(table, Item(attrS of "hello"), consistentRead = true).successValue().item!!
            assertThat(attrN(updatedItem), equalTo(321))

            val query = query(
                table,
                keyConditionExpression = "${attrS.name} = :v1",
                expressionAttributeValues = mapOf(":v1" to Str("hello"))
            ).successValue().items

            assertThat(attrN[query.first()], equalTo(321))

            val scan = scan(table).successValue().items

            assertThat(attrN[scan.first()], equalTo(321))

            deleteItem(table, Item(attrS of "hello")).successValue()
        }
    }

    private fun item(key: String) = Item(
        attrS of key,
        attrBool of true,
        attrB of Base64Blob.encode("foo"),
        attrBS of setOf(Base64Blob.encode("bar")),
        attrN of 123,
        attrNS of setOf(123, 321),
        attrL of listOf(List(listOf(Str("foo"))), Num(123), Null()),
        attrM of Item(attrS of "foo", attrBool of false),
        attrSS of setOf("345", "567"),
        attrNL of null
    )

    @Test
    fun `table lifecycle`() {
        with(dynamo) {
            assertThat(listTables().successValue().TableNames, hasElement(table))

            assertThat(describeTable(table).successValue().Table.ItemCount, equalTo(0))

            assertThat(
                updateTable(
                    table,
                    billingMode = PROVISIONED,
                    provisionedThroughput = ProvisionedThroughput(1, 1)
                ).successValue()
                    .TableDescription.BillingModeSummary?.BillingMode,
                equalTo(PROVISIONED)
            )

            waitForUpdate()
        }
    }

    private fun delete() = """DELETE FROM "$table" WHERE "$attrS" = "hello";"""
    private fun statement() = """SELECT "$attrS" FROM "$table" WHERE "$attrS" = "hello";"""

    private fun DynamoDb.createTable(tableName: TableName, keyAttr: AttributeName) = createTable(
        tableName, listOf(KeySchema(keyAttr, HASH)), listOf(AttributeDefinition(keyAttr, S)),
        billingMode = PAY_PER_REQUEST
    ).successValue()

    private fun waitForUpdate() = Thread.sleep(duration.toMillis())

    @Test
    @Disabled
    fun `delete tables`() {
        dynamo.listTables()
            .successValue()
            .TableNames
            .filter { it.value.startsWith("http4k-connect") }
            .forEach {
                println("DELETING$it")
                dynamo.deleteTable(it)
            }
    }
}
