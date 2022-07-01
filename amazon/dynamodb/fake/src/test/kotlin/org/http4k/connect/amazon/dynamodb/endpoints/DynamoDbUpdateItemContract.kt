package org.http4k.connect.amazon.dynamodb.endpoints

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.DynamoDbSource
import org.http4k.connect.amazon.dynamodb.FakeDynamoDbSource
import org.http4k.connect.amazon.dynamodb.LocalDynamoDbSource
import org.http4k.connect.amazon.dynamodb.attrN
import org.http4k.connect.amazon.dynamodb.attrS
import org.http4k.connect.amazon.dynamodb.attrSS
import org.http4k.connect.amazon.dynamodb.createItem
import org.http4k.connect.amazon.dynamodb.createTable
import org.http4k.connect.amazon.dynamodb.getItem
import org.http4k.connect.amazon.dynamodb.model.BillingMode
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.KeySchema
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.asAttributeDefinition
import org.http4k.connect.amazon.dynamodb.model.compound
import org.http4k.connect.amazon.dynamodb.model.with
import org.http4k.connect.amazon.dynamodb.putItem
import org.http4k.connect.amazon.dynamodb.sample
import org.http4k.connect.amazon.dynamodb.updateItem
import org.http4k.connect.successValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class DynamoDbUpdateItemContract {

    abstract val dynamo: DynamoDb

    companion object {
        private val table = TableName.sample()

        private val item = createItem("hash1", 1)
        private val key = Item(attrS of "hash1")
    }

    @BeforeEach
    fun createTable() {
        dynamo.createTable(
            table,
            KeySchema = KeySchema.compound(attrS.name),
            AttributeDefinitions = listOf(attrS.asAttributeDefinition()),
            BillingMode = BillingMode.PAY_PER_REQUEST
        ).successValue()

        dynamo.waitForExist(table)
    }

    @Test
    fun `set value on existing item`() {
        dynamo.putItem(TableName = table, Item = item).successValue()

        dynamo.updateItem(
            TableName = table,
            Key = key,
            UpdateExpression = "SET $attrN = :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrN.asValue(999))
        ).successValue()

        assertThat(getItem(), equalTo(item.with(attrN of 999)))
    }

    @Test
    fun `set value on missing item`() {
        dynamo.updateItem(
            TableName = table,
            Key = key,
            UpdateExpression = "SET $attrN = :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrN.asValue(999))
        ).successValue()

        assertThat(getItem(), equalTo(Item(attrS of "hash1", attrN of 999)))
    }

    @Test
    fun `increment value on existing item`() {
        dynamo.putItem(TableName = table, Item = item).successValue()

        dynamo.updateItem(
            TableName = table,
            Key = key,
            UpdateExpression = "SET $attrN = $attrN + :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrN.asValue(2))
        ).successValue()

        assertThat(getItem(), equalTo(item.with(attrN of 3)))
    }

    @Test
    fun `remove attribute from existing item`() {
        dynamo.putItem(TableName = table, Item = item).successValue()

        dynamo.updateItem(
            TableName = table,
            Key = key,
            UpdateExpression = "REMOVE $attrN",
        ).successValue()

        assertThat(getItem(), equalTo(item - attrN.name))
    }

    @Test
    fun `remove attribute from missing item`() {
        dynamo.updateItem(
            TableName = table,
            Key = key,
            UpdateExpression = "REMOVE $attrN",
        ).successValue()

        assertThat(getItem(), equalTo(key))
    }

    @Test
    fun `add element to set`() {
        dynamo.putItem(TableName = table, Item = item).successValue()

        dynamo.updateItem(
            TableName = table,
            Key = key,
            UpdateExpression = "ADD $attrSS :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrSS.asValue(setOf("foo")))
        ).successValue()

        assertThat(getItem(), equalTo(item.with(attrSS of setOf("345", "567", "foo"))))
    }

    @Test
    fun `delete element from set`() {
        dynamo.putItem(TableName = table, Item = item).successValue()

        dynamo.updateItem(
            TableName = table,
            Key = key,
            UpdateExpression = "DELETE $attrSS :val1",
            ExpressionAttributeValues = mapOf(":val1" to attrSS.asValue(setOf("345")))
        ).successValue()

        assertThat(getItem(), equalTo(item.with(attrSS of setOf("567"))))
    }

    private fun getItem(): Item? = dynamo.getItem(
        TableName = table,
        Key = Item(attrS of "hash1")
    ).successValue().item
}

class FakeDynamoDbUpdateItemTest: DynamoDbUpdateItemContract(), DynamoDbSource by FakeDynamoDbSource()
class LocalDynamoDbUpdateItemTest: DynamoDbUpdateItemContract(), DynamoDbSource by LocalDynamoDbSource()
