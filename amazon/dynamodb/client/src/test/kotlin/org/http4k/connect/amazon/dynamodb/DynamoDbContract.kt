package org.http4k.connect.amazon.dynamodb

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasElement
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.Bool
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.List
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.Null
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.Num
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.Str
import org.http4k.connect.amazon.dynamodb.action.ProvisionedThroughput
import org.http4k.connect.amazon.dynamodb.action.TransactGetItem.Companion.Get
import org.http4k.connect.amazon.dynamodb.action.TransactWriteItem.Companion.Delete
import org.http4k.connect.amazon.dynamodb.action.TransactWriteItem.Companion.Put
import org.http4k.connect.amazon.dynamodb.action.TransactWriteItem.Companion.Update
import org.http4k.connect.amazon.model.Attribute
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.BillingMode.PAY_PER_REQUEST
import org.http4k.connect.amazon.model.BillingMode.PROVISIONED
import org.http4k.connect.amazon.model.KeyType.HASH
import org.http4k.connect.amazon.model.TableName
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Duration
import java.util.UUID

abstract class DynamoDbContract(
    http: HttpHandler,
    private val duration: Duration = Duration.ofSeconds(10)
) : AwsContract(http) {

    private val dynamo by lazy {
        DynamoDb.Http(aws.region, { aws.credentials }, http)
    }

    private val table = TableName.of(UUID.randomUUID().toString())

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
    private val attrMissing = Attribute.string("theMissing")

    @BeforeEach
    fun create() {
        assertThat(dynamo.createTable(table, attrS).TableDescription.ItemCount, equalTo(0))
        waitForUpdate()
    }

    @BeforeEach
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
                        mapOf(attrS to "hello"),
                        "SET $attrBool = :c",
                        ExpressionAttributeValues = mapOf(":c" to Bool(true))
                    ),
                    Put(table, item("hello2")),
                    Put(table, item("hello3")),
                    Delete(table, mapOf(attrS to "hello4"))
                )
            ).successValue()

            val result = transactGetItems(
                listOf(
                    Get(table, mapOf(attrS to "hello2")),
                    Get(table, mapOf(attrS to "hello3"))
                )
            ).successValue()

            assertThat(attrS[result.responses[0]], equalTo("hello2"))
            assertThat(attrS[result.responses[1]], equalTo("hello3"))
        }
    }

    @Test
    @Disabled
    fun `item lifecycle`() {
        with(dynamo) {
            putItem(
                table, item = item("hello")
            ).successValue()

            val item = getItem(table, mapOf(attrS to "hello")).successValue().item

            assertThat(attrS[item], equalTo("hello"))
            assertThat(attrBool[item], equalTo(true))
            assertThat(attrB[item], equalTo(Base64Blob.encode("foo")))
            assertThat(attrBS[item], equalTo(setOf(Base64Blob.encode("bar"))))
            assertThat(attrN[item], equalTo(BigDecimal(123)))
            assertThat(attrNS[item], equalTo(setOf(BigDecimal(123), BigDecimal("12.34"))))
            assertThat(attrL[item], equalTo(listOf(List(listOf(Str("foo"))), Num(123), Null())))
            assertThat(attrM[item], equalTo(mapOf(attrS to "foo", attrBool to false)))
            assertThat(attrSS[item], equalTo(setOf("345", "567")))
            assertThat(attrNL[item], absent())
            assertThat(attrMissing[item], absent())

            updateItem(
                table,
                mapOf(attrS to "hello"),
                null,
                "set $attrN = :val1",
                expressionAttributeValues = mapOf(":val1" to Num(321))
            ).successValue()

            val updatedItem = getItem(table, mapOf(attrS to "hello"), consistentRead = true).successValue().item
            assertThat(attrN[updatedItem], equalTo(BigDecimal(321)))

            val query = query(
                table,
                keyConditionExpression = "$attrS = :v1",
                expressionAttributeValues = mapOf(
                    ":v1" to Str("hello")
                )
            ).successValue().items

            assertThat(attrN[query.first()], equalTo(BigDecimal(321)))

            deleteItem(table, mapOf(attrS to "hello")).successValue()
        }
    }

    private fun item(key: String) = mapOf(
        attrS to key,
        attrBool to true,
        attrB to Base64Blob.encode("foo"),
        attrBS to setOf(Base64Blob.encode("bar")),
        attrN to 123,
        attrNS to setOf(123, 12.34),
        attrL to listOf(List(listOf(Str("foo"))), Num(123), Null()),
        attrM to mapOf(attrS to "foo", attrBool to false),
        attrSS to setOf("345", "567"),
        attrNL to null
    )

    @Test
    @Disabled
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

    private fun DynamoDb.createTable(tableName: TableName, keyAttr: Attribute<*, *>) = createTable(
        tableName, listOf(keyAttr.keySchema(HASH)), listOf(keyAttr.attrDefinition()),
        billingMode = PAY_PER_REQUEST
    ).successValue()

    private fun waitForUpdate() = Thread.sleep(duration.toMillis())

//    @Test
//    fun deleteTables() {
//        dynamo.listTables()
//            .successValue()
//            .TableNames.forEach {
//                println("DELETING$it")
//                dynamo.deleteTable(it)
//            }
//    }
}
