package org.http4k.connect.amazon.dynamodb.endpoints

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasElement
import com.natpryce.hamkrest.hasSize
import org.http4k.connect.amazon.dynamodb.DynamoDbSource
import org.http4k.connect.amazon.dynamodb.FakeDynamoDbSource
import org.http4k.connect.amazon.dynamodb.LocalDynamoDbSource
import org.http4k.connect.amazon.dynamodb.attrN
import org.http4k.connect.amazon.dynamodb.attrS
import org.http4k.connect.amazon.dynamodb.createItem
import org.http4k.connect.amazon.dynamodb.createTable
import org.http4k.connect.amazon.dynamodb.model.BillingMode
import org.http4k.connect.amazon.dynamodb.model.KeySchema
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.asAttributeDefinition
import org.http4k.connect.amazon.dynamodb.model.compound
import org.http4k.connect.amazon.dynamodb.putItem
import org.http4k.connect.amazon.dynamodb.sample
import org.http4k.connect.amazon.dynamodb.scan
import org.http4k.connect.successValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class DynamoDbScanContract: DynamoDbSource {

    private val table = TableName.sample()
    private val item1 = createItem("hash1", 1)
    private val item2 = createItem("hash2", 1)
    private val item3 = createItem("hash3", 2)

    @BeforeEach
    fun createTable() {
        dynamo.createTable(
            table,
            KeySchema = KeySchema.compound(attrS.name),
            AttributeDefinitions = listOf(attrS.asAttributeDefinition()),
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
    }

    @Test
    fun `scan with limit`() {
        val result = dynamo.scan(TableName = table, Limit = 2).successValue()

        assertThat(result.Count, equalTo(2))
        assertThat(result.items, hasSize(equalTo(2)))
    }
}

class LocalDynamoDbScanTest: DynamoDbScanContract(), DynamoDbSource by LocalDynamoDbSource()
class FakeDynamoDbScanTest: DynamoDbScanContract(), DynamoDbSource by FakeDynamoDbSource()
