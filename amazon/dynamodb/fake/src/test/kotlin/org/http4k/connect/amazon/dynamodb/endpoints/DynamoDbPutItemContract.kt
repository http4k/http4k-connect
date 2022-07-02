package org.http4k.connect.amazon.dynamodb.endpoints

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.dynamodb.DynamoDbSource
import org.http4k.connect.amazon.dynamodb.FakeDynamoDbSource
import org.http4k.connect.amazon.dynamodb.LocalDynamoDbSource
import org.http4k.connect.amazon.dynamodb.attrN
import org.http4k.connect.amazon.dynamodb.attrS
import org.http4k.connect.amazon.dynamodb.createTable
import org.http4k.connect.amazon.dynamodb.getItem
import org.http4k.connect.amazon.dynamodb.model.BillingMode
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.KeySchema
import org.http4k.connect.amazon.dynamodb.model.TableName
import org.http4k.connect.amazon.dynamodb.model.asAttributeDefinition
import org.http4k.connect.amazon.dynamodb.model.compound
import org.http4k.connect.amazon.dynamodb.putItem
import org.http4k.connect.amazon.dynamodb.sample
import org.http4k.connect.successValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

abstract class DynamoDbPutItemContract: DynamoDbSource {

    private val table = TableName.sample()

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
    fun `put item`() {
        val item = Item(attrS of "hash1", attrN of 1)

        dynamo.putItem(table, item)

        assertThat(
            dynamo.getItem(table, Key(attrS of "hash1")).successValue().item,
            equalTo(item)
        )
    }

    @Test
    fun `replace item`() {
        val original = Item(attrS of "hash1", attrN of 1)
        val updated = Item(attrS of "hash1", attrN of 2)

        dynamo.putItem(table, original)
        dynamo.putItem(table, updated)

        assertThat(
            dynamo.getItem(table, Key(attrS of "hash1")).successValue().item,
            equalTo(updated)
        )
    }
}

class FakeDynamoDbPutItemContract: DynamoDbPutItemContract(), DynamoDbSource by FakeDynamoDbSource()
class LocalDynamoDbPutItemContract: DynamoDbPutItemContract(), DynamoDbSource by LocalDynamoDbSource()
