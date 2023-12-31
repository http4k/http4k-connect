package org.http4k.connect.amazon.dynamodb.endpoints

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasElement
import com.natpryce.hamkrest.hasSize
import com.natpryce.hamkrest.lessThan
import com.natpryce.hamkrest.present
import org.http4k.connect.amazon.dynamodb.DynamoDbSource
import org.http4k.connect.amazon.dynamodb.FakeDynamoDbSource
import org.http4k.connect.amazon.dynamodb.LocalDynamoDbSource
import org.http4k.connect.amazon.dynamodb.attrB
import org.http4k.connect.amazon.dynamodb.attrBool
import org.http4k.connect.amazon.dynamodb.attrN
import org.http4k.connect.amazon.dynamodb.attrS
import org.http4k.connect.amazon.dynamodb.attrSS
import org.http4k.connect.amazon.dynamodb.batchWriteItem
import org.http4k.connect.amazon.dynamodb.createItem
import org.http4k.connect.amazon.dynamodb.createTable
import org.http4k.connect.amazon.dynamodb.model.BillingMode
import org.http4k.connect.amazon.dynamodb.model.GlobalSecondaryIndex
import org.http4k.connect.amazon.dynamodb.model.IndexName
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.KeySchema
import org.http4k.connect.amazon.dynamodb.model.Projection
import org.http4k.connect.amazon.dynamodb.model.ReqWriteItem
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.asAttributeDefinition
import org.http4k.connect.amazon.dynamodb.model.compound
import org.http4k.connect.amazon.dynamodb.model.without
import org.http4k.connect.amazon.dynamodb.putItem
import org.http4k.connect.amazon.dynamodb.sample
import org.http4k.connect.amazon.dynamodb.scan
import org.http4k.connect.model.Base64Blob
import org.http4k.connect.successValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class DynamoDbScanContract: DynamoDbSource {

    private val table = TableName.sample()
    private val item1 = createItem("hash1", 1, Base64Blob.encode("foo"))
    private val item2 = createItem("hash2", 1, Base64Blob.encode("bar"))
    private val item3 = createItem("hash3", 2).without(attrB)

    private val binaryStringGSI = IndexName.of("bin-string")

    @BeforeEach
    fun createTable() {
        dynamo.createTable(
            table,
            KeySchema = KeySchema.compound(attrS.name),
            AttributeDefinitions = listOf(
                attrS.asAttributeDefinition(),
                attrB.asAttributeDefinition()
            ),
            GlobalSecondaryIndexes = listOf(
                GlobalSecondaryIndex(IndexName = binaryStringGSI, KeySchema.compound(attrB.name, attrS.name), Projection.all)
            ),
            BillingMode = BillingMode.PAY_PER_REQUEST
        ).successValue()

        dynamo.waitForExist(table)

        dynamo.putItem(table, item1).successValue()
        dynamo.putItem(table, item2).successValue()
        dynamo.putItem(table, item3).successValue()
    }

    @Test
    fun `scan table`() {
        val result = dynamo.scan(table).successValue()

        assertThat(result.Count, equalTo(3))
        assertThat(result.items, hasSize(equalTo(3)))
        assertThat(result.items, hasElement(item1))
        assertThat(result.items, hasElement(item2))
        assertThat(result.items, hasElement(item3))
        assertThat(result.LastEvaluatedKey, absent())
    }

    @Test
    fun `scan with filter`() {
        val result = dynamo.scan(
            TableName = table,
            FilterExpression = "$attrN = :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrN.asValue(1))
        ).successValue()

        assertThat(result.Count, equalTo(2))
        assertThat(result.items, hasSize(equalTo(2)))
        assertThat(result.items, hasElement(item1))
        assertThat(result.items, hasElement(item2))
        assertThat(result.LastEvaluatedKey, absent())
    }

    @Test
    fun `scan with limit`() {
        val result = dynamo.scan(TableName = table, Limit = 2).successValue()

        assertThat(result.Count, equalTo(2))
        assertThat(result.items, hasSize(equalTo(2)))
        assertThat(result.LastEvaluatedKey, present())
    }

    @Test
    fun `scan multiple pages`() {
        val page1 = dynamo.scan(
            TableName = table,
            Limit = 2
        ).successValue()

        assertThat(page1.Count, equalTo(2))
        assertThat(page1.items, hasSize(equalTo(2)))
        assertThat(page1.LastEvaluatedKey, present())

        val page2 = dynamo.scan(
            TableName = table,
            ExclusiveStartKey = page1.LastEvaluatedKey
        ).successValue()

        assertThat(page2.Count, equalTo(1))
        page2.items.forEach { assertThat(page1.items, hasElement(it).not()) }
        assertThat(page2.LastEvaluatedKey, absent())
    }

    @Test
    fun `scan with max results for page`() {
        val numItems = 2_000
        val payload = (1 .. 1_000).map { "a".repeat(1_000) }.toSet()

        (1..numItems).chunked(25).forEach { chunk ->
            dynamo.batchWriteItem(
                mapOf(
                    table to chunk.map { index ->
                        ReqWriteItem.Put(Item(attrS of "hash$index", attrSS of payload))
                    }
                )
            ).successValue()
        }

        val result = dynamo.scan(
            TableName = table
        ).successValue()

        assertThat(result.Count, present(lessThan(numItems)))
        assertThat(result.LastEvaluatedKey, present())
    }

    @Test
    fun `scan index`() {
        val result = dynamo.scan(TableName = table, IndexName = binaryStringGSI).successValue()

        assertThat(result.Count, equalTo(2))
        assertThat(result.items, hasSize(equalTo(2)))
        assertThat(result.items, hasElement(item1))
        assertThat(result.items, hasElement(item2))
        assertThat(result.LastEvaluatedKey, absent())
    }

    @Test // Fixes GH#327
    fun `filter evaluated after pagination`() {
        dynamo.batchWriteItem(
            mapOf(
                table to listOf(
                    ReqWriteItem.Put(Item(attrS of "hash1", attrBool of true)),
                    ReqWriteItem.Put(Item(attrS of "hash2", attrBool of true)),
                    ReqWriteItem.Put(Item(attrS of "hash3", attrBool of false)),
                    ReqWriteItem.Put(Item(attrS of "hash4", attrBool of false)),
                    ReqWriteItem.Put(Item(attrS of "hash5", attrBool of false))
                )
            )
        ).successValue()

        val result = dynamo.scan(
            TableName = table,
            FilterExpression = "$attrBool = :val1",
            ExpressionAttributeValues = mapOf(
                ":val1" to attrBool.asValue(true)
            ),
            Limit = 4,
        ).successValue()

        assertThat(result.Count, present(equalTo(2)))
        assertThat(result.items, equalTo(listOf(
            Item(attrS of "hash1", attrBool of true),
            Item(attrS of "hash2", attrBool of true)
        )))
        assertThat(result.LastEvaluatedKey, equalTo(Item(attrS of "hash4")))
    }
}

class LocalDynamoDbScanTest: DynamoDbScanContract(), DynamoDbSource by LocalDynamoDbSource()
class FakeDynamoDbScanTest: DynamoDbScanContract(), DynamoDbSource by FakeDynamoDbSource()
