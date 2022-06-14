package org.http4k.connect.amazon.dynamodb.endpoints

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.core.model.Base64Blob
import org.http4k.connect.amazon.dynamodb.DynamoDbSource
import org.http4k.connect.amazon.dynamodb.FakeDynamoDbSource
import org.http4k.connect.amazon.dynamodb.LocalDynamoDbSource
import org.http4k.connect.amazon.dynamodb.attrB
import org.http4k.connect.amazon.dynamodb.attrN
import org.http4k.connect.amazon.dynamodb.attrS
import org.http4k.connect.amazon.dynamodb.createItem
import org.http4k.connect.amazon.dynamodb.createTable
import org.http4k.connect.amazon.dynamodb.deleteTable
import org.http4k.connect.amazon.dynamodb.model.BillingMode
import org.http4k.connect.amazon.dynamodb.model.GlobalSecondaryIndex
import org.http4k.connect.amazon.dynamodb.model.IndexName
import org.http4k.connect.amazon.dynamodb.model.KeySchema
import org.http4k.connect.amazon.dynamodb.model.LocalSecondaryIndexes
import org.http4k.connect.amazon.dynamodb.model.Projection
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.asAttributeDefinition
import org.http4k.connect.amazon.dynamodb.model.compound
import org.http4k.connect.amazon.dynamodb.putItem
import org.http4k.connect.amazon.dynamodb.query
import org.http4k.connect.amazon.dynamodb.sample
import org.http4k.connect.successValue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class DynamoDbQueryContract: DynamoDbSource {

    private val table = TableName.sample()

    companion object {
        private val hash1Val1 = createItem("hash1", 1, Base64Blob.encode("spam"))
        private val hash1Val2 = createItem("hash1", 2, Base64Blob.encode("ham"))
        private val hash2Val1 = createItem("hash2", 1, Base64Blob.encode("eggs"))

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
                GlobalSecondaryIndex(IndexName = numbersIndex, KeySchema.compound(attrN.name, attrS.name), Projection.all)
            ),
            LocalSecondaryIndexes = listOf(
                LocalSecondaryIndexes(IndexName = stringAndBinaryIndex, KeySchema.compound(attrS.name, attrB.name), Projection.all)
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
        assertThat(result.items, equalTo(listOf(
            hash1Val1,
            hash1Val2
        )))
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
        assertThat(result.items, equalTo(listOf(
            hash1Val2,
            hash1Val1
        )))
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
        assertThat(result.items, equalTo(listOf(
            hash1Val2
        )))
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
        assertThat(result.items, equalTo(listOf(
            hash1Val1,
            hash2Val1
        )))
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
        assertThat(result.items, equalTo(listOf(
            hash2Val1,
            hash1Val1
        )))
    }

    @Test
    fun `query by local index`() {
        dynamo.putItem(table, hash1Val1)
        dynamo.putItem(table, hash1Val2)
        dynamo.putItem(table, hash2Val1)

        val result = dynamo.query(
            TableName = table,
            IndexName = stringAndBinaryIndex,
            KeyConditionExpression = "$attrS = :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrS.asValue("hash1"))
        ).successValue()

        assertThat(result.Count, equalTo(2))
        assertThat(result.items, equalTo(listOf(
            hash1Val2,
            hash1Val1,
        )))
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
        assertThat(result.items, equalTo(listOf(
            hash1Val1,
            hash1Val2,
        )))
    }
}

class FakeDynamoDbQueryTest: DynamoDbQueryContract(), DynamoDbSource by FakeDynamoDbSource()
class LocalDynamoDbQueryTest: DynamoDbQueryContract(), DynamoDbSource by LocalDynamoDbSource()
