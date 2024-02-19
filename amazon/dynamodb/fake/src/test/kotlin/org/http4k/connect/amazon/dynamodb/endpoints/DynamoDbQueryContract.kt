package org.http4k.connect.amazon.dynamodb.endpoints

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
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
import org.http4k.connect.amazon.dynamodb.deleteTable
import org.http4k.connect.amazon.dynamodb.model.BillingMode
import org.http4k.connect.amazon.dynamodb.model.GlobalSecondaryIndex
import org.http4k.connect.amazon.dynamodb.model.IndexName
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.KeySchema
import org.http4k.connect.amazon.dynamodb.model.LocalSecondaryIndex
import org.http4k.connect.amazon.dynamodb.model.Projection
import org.http4k.connect.amazon.dynamodb.model.ReqWriteItem
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.asAttributeDefinition
import org.http4k.connect.amazon.dynamodb.model.compound
import org.http4k.connect.amazon.dynamodb.model.without
import org.http4k.connect.amazon.dynamodb.putItem
import org.http4k.connect.amazon.dynamodb.query
import org.http4k.connect.amazon.dynamodb.sample
import org.http4k.connect.model.Base64Blob
import org.http4k.connect.successValue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class DynamoDbQueryContract : DynamoDbSource {

    private val table = TableName.sample()

    companion object {
        private val hash1Val1 = createItem("hash1", 1, Base64Blob.encode("spam"))
        private val hash1Val2 = createItem("hash1", 2, Base64Blob.encode("ham"))
        private val hash2Val1 = createItem("hash2", 1, Base64Blob.encode("eggs"))
        private val hash1Val3WithoutBinary = createItem("hash1", 3).without(attrB)

        private val numbersIndex = IndexName.of("numbers")
        private val stringAndBinaryIndex = IndexName.of("string-bin")
    }

    @BeforeEach
    fun createTable() {
        dynamo.createTable(
            table,
            KeySchema = KeySchema.compound(attrS.name, attrN.name),
            AttributeDefinitions = listOf(
                attrS.asAttributeDefinition(),
                attrN.asAttributeDefinition(),
                attrB.asAttributeDefinition()
            ),
            GlobalSecondaryIndexes = listOf(
                GlobalSecondaryIndex(
                    IndexName = numbersIndex,
                    KeySchema.compound(attrN.name, attrS.name),
                    Projection.all
                )
            ),
            LocalSecondaryIndexes = listOf(
                LocalSecondaryIndex(
                    IndexName = stringAndBinaryIndex,
                    KeySchema.compound(attrS.name, attrB.name),
                    Projection.all
                )
            ),
            BillingMode = BillingMode.PAY_PER_REQUEST
        ).successValue()

        dynamo.waitForExist(table)
    }

    @AfterEach
    fun deleteTable() {
        dynamo.deleteTable(table)
    }

    @Test
    fun `query empty table`() {
        val result = dynamo.query(
            TableName = table,
            KeyConditionExpression = "$attrS = :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrS.asValue("hash1"))
        ).successValue()

        assertThat(result.Count, equalTo(0))
        assertThat(result.items, equalTo(emptyList()))
    }

    @Test
    fun `query by hash`() {
        dynamo.putItem(table, hash1Val1)
        dynamo.putItem(table, hash1Val2)
        dynamo.putItem(table, hash2Val1)

        val result = dynamo.query(
            TableName = table,
            KeyConditionExpression = "$attrS = :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrS.asValue("hash1"))
        ).successValue()

        assertThat(result.Count, equalTo(2))
        assertThat(
            result.items, equalTo(
                listOf(
                    hash1Val1,
                    hash1Val2
                )
            )
        )
    }

    @Test
    fun `query by hash - reverse order`() {
        dynamo.putItem(table, hash1Val1)
        dynamo.putItem(table, hash1Val2)
        dynamo.putItem(table, hash2Val1)

        val result = dynamo.query(
            TableName = table,
            KeyConditionExpression = "$attrS = :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrS.asValue("hash1")),
            ScanIndexForward = false
        ).successValue()

        assertThat(result.Count, equalTo(2))
        assertThat(
            result.items, equalTo(
                listOf(
                    hash1Val2,
                    hash1Val1
                )
            )
        )
    }

    @Test
    fun `query by hash and range`() {
        dynamo.putItem(table, hash1Val1)
        dynamo.putItem(table, hash1Val2)
        dynamo.putItem(table, hash2Val1)

        val result = dynamo.query(
            TableName = table,
            KeyConditionExpression = "$attrS = :val1 AND $attrN > :val2",
            ExpressionAttributeValues = mapOf(
                ":val1" to attrS.asValue("hash1"),
                ":val2" to attrN.asValue(1)
            )
        ).successValue()

        assertThat(result.Count, equalTo(1))
        assertThat(
            result.items, equalTo(
                listOf(
                    hash1Val2
                )
            )
        )
    }

    @Test
    fun `query by global index`() {
        dynamo.putItem(table, hash1Val1)
        dynamo.putItem(table, hash1Val2)
        dynamo.putItem(table, hash2Val1)

        val result = dynamo.query(
            TableName = table,
            IndexName = numbersIndex,
            KeyConditionExpression = "$attrN = :val1",
            ExpressionAttributeValues = mapOf(
                ":val1" to attrN.asValue(1)
            )
        ).successValue()

        assertThat(result.Count, equalTo(2))
        assertThat(
            result.items, equalTo(
                listOf(
                    hash1Val1,
                    hash2Val1
                )
            )
        )
    }

    @Test
    fun `query by global index - reverse`() {
        dynamo.putItem(table, hash1Val1)
        dynamo.putItem(table, hash1Val2)
        dynamo.putItem(table, hash2Val1)

        val result = dynamo.query(
            TableName = table,
            IndexName = numbersIndex,
            KeyConditionExpression = "$attrN = :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrN.asValue(1)),
            ScanIndexForward = false
        ).successValue()

        assertThat(result.Count, equalTo(2))
        assertThat(
            result.items, equalTo(
                listOf(
                    hash2Val1,
                    hash1Val1
                )
            )
        )
    }

    @Test
    fun `query by local index`() {
        dynamo.putItem(table, hash1Val1)
        dynamo.putItem(table, hash1Val2)
        dynamo.putItem(table, hash2Val1)
        dynamo.putItem(table, hash1Val3WithoutBinary)

        val result = dynamo.query(
            TableName = table,
            IndexName = stringAndBinaryIndex,
            KeyConditionExpression = "$attrS = :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrS.asValue("hash1"))
        ).successValue()

        assertThat(result.Count, equalTo(2))
        assertThat(
            result.items, equalTo(
                listOf(
                    hash1Val2,
                    hash1Val1,
                )
            )
        )
    }

    @Test
    fun `query by local index - reverse`() {
        dynamo.putItem(table, hash1Val1)
        dynamo.putItem(table, hash1Val2)
        dynamo.putItem(table, hash2Val1)

        val result = dynamo.query(
            TableName = table,
            IndexName = stringAndBinaryIndex,
            KeyConditionExpression = "$attrS = :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrS.asValue("hash1")),
            ScanIndexForward = false
        ).successValue()

        assertThat(result.Count, equalTo(2))
        assertThat(
            result.items, equalTo(
                listOf(
                    hash1Val1,
                    hash1Val2,
                )
            )
        )
    }

    @Test
    fun `query with limit`() {
        dynamo.putItem(table, hash1Val1)
        dynamo.putItem(table, hash1Val2)
        dynamo.putItem(table, hash2Val1)

        val result = dynamo.query(
            TableName = table,
            KeyConditionExpression = "$attrS = :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrS.asValue("hash1")),
            Limit = 1
        ).successValue()

        assertThat(result.Count, equalTo(1))
        assertThat(result.items, equalTo(listOf(hash1Val1)))
        assertThat(result.LastEvaluatedKey, equalTo(Item(attrS of "hash1", attrN of 1)))
    }

    @Test
    fun `query with ExclusiveStartKey`() {
        val item1 = Item(attrS of "hash1", attrN of 1).also { dynamo.putItem(table, it) }
        val item2 = Item(attrS of "hash1", attrN of 2).also { dynamo.putItem(table, it) }
        val item3 = Item(attrS of "hash1", attrN of 3).also { dynamo.putItem(table, it) }

        val result = dynamo.query(
            TableName = table,
            KeyConditionExpression = "$attrS = :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrS.asValue("hash1")),
            ExclusiveStartKey = item1,
        ).successValue()

        assertThat(result.Count, equalTo(2))
        assertThat(result.items, equalTo(listOf(item2, item3)))
        assertThat(result.LastEvaluatedKey, absent())
    }

    @Test
    fun `query with ExclusiveStartKey - empty`() {
        Item(attrS of "hash1", attrN of 1).also { dynamo.putItem(table, it) }
        Item(attrS of "hash1", attrN of 2).also { dynamo.putItem(table, it) }
        val item3 = Item(attrS of "hash1", attrN of 3).also { dynamo.putItem(table, it) }

        val result = dynamo.query(
            TableName = table,
            KeyConditionExpression = "$attrS = :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrS.asValue("hash1")),
            ExclusiveStartKey = item3,
        ).successValue()

        assertThat(result.Count, equalTo(0))
        assertThat(result.items, equalTo(emptyList()))
        assertThat(result.LastEvaluatedKey, absent())
    }

    @Test
    fun `query by index - with limit`() {
        dynamo.putItem(table, hash1Val1)
        dynamo.putItem(table, hash1Val2)
        dynamo.putItem(table, hash2Val1)

        val result = dynamo.query(
            TableName = table,
            IndexName = numbersIndex,
            KeyConditionExpression = "$attrN = :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrN.asValue(1)),
            ScanIndexForward = false,
            Limit = 1
        ).successValue()

        assertThat(result.Count, equalTo(1))
        assertThat(
            result.items, equalTo(
                listOf(
                    hash2Val1,
                )
            )
        )
        // ensure next key matches current index
        assertThat(
            result.LastEvaluatedKey, equalTo(
                mapOf(
                    attrN.name to hash2Val1[attrN.name]!!,
                    attrS.name to hash2Val1[attrS.name]!!
                )
            )
        )
    }

    @Test
    fun `query with max results for page`() {
        val numItems = 2_000
        val payload = (1..1_000).map { "a".repeat(1_000) }.toSet()

        (1..numItems).chunked(25).forEach { chunk ->
            dynamo.batchWriteItem(
                mapOf(
                    table to chunk.map { index ->
                        ReqWriteItem.Put(Item(attrS of "hash1", attrN of index, attrSS of payload))
                    }
                )
            ).successValue()
        }

        val result = dynamo.query(
            TableName = table,
            KeyConditionExpression = "$attrS = :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrS.asValue("hash1")),
        ).successValue()

        assertThat(result.Count, present(lessThan(numItems)))
        assertThat(result.LastEvaluatedKey, present())
    }

    @Test // Fixes GH#327
    fun `filter evaluated after pagination`() {
        dynamo.batchWriteItem(
            mapOf(
                table to listOf(
                    ReqWriteItem.Put(Item(attrS of "hash1", attrN of 1, attrBool of true)),
                    ReqWriteItem.Put(Item(attrS of "hash1", attrN of 2, attrBool of true)),
                    ReqWriteItem.Put(Item(attrS of "hash1", attrN of 3, attrBool of false)),
                    ReqWriteItem.Put(Item(attrS of "hash1", attrN of 4, attrBool of false)),
                    ReqWriteItem.Put(Item(attrS of "hash1", attrN of 5, attrBool of false))
                )
            )
        ).successValue()

        val result = dynamo.query(
            TableName = table,
            KeyConditionExpression = "$attrS = :val1",
            FilterExpression = "$attrBool = :val2",
            ExpressionAttributeValues = mapOf(
                ":val1" to attrS.asValue("hash1"),
                ":val2" to attrBool.asValue(true)
            ),
            Limit = 4,
        ).successValue()

        assertThat(result.Count, present(equalTo(2)))
        assertThat(
            result.items, equalTo(
                listOf(
                    Item(attrS of "hash1", attrN of 1, attrBool of true),
                    Item(attrS of "hash1", attrN of 2, attrBool of true)
                )
            )
        )
        assertThat(result.LastEvaluatedKey, equalTo(Item(attrS of "hash1", attrN of 4)))
    }
}


class FakeDynamoDbQueryTest : DynamoDbQueryContract(), DynamoDbSource by FakeDynamoDbSource()
class LocalDynamoDbQueryTest : DynamoDbQueryContract(), DynamoDbSource by LocalDynamoDbSource()
