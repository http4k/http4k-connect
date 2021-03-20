package org.http4k.connect.amazon.dynamodb

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.Num
import org.http4k.connect.amazon.dynamodb.action.AttributeValue.Companion.Str
import org.http4k.connect.amazon.model.AttributeName
import org.http4k.connect.amazon.model.BillingMode.PAY_PER_REQUEST
import org.http4k.connect.amazon.model.BillingMode.PROVISIONED
import org.http4k.connect.amazon.model.ProvisionedThroughput
import org.http4k.connect.amazon.model.TableName
import org.http4k.connect.amazon.model.hashKeySchema
import org.http4k.connect.amazon.model.stringAttrDefinition
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class DynamoDbContract(http: HttpHandler) : AwsContract(http) {
    private val dynamo by lazy {
        DynamoDb.Http(aws.region, { aws.credentials }, http)
    }

    private val table = TableName.of(UUID.randomUUID().toString())

    @Test
    fun `table lifecycle`() {
        dynamo.listTables().successValue()

        try {
            val string = AttributeName.of("theString")
            val number = AttributeName.of("theNum")

            dynamo.createTable(
                table,
                listOf(string.hashKeySchema()),
                listOf(
                    string.stringAttrDefinition()
                ),
                billingMode = PAY_PER_REQUEST
            ).successValue()

            Thread.sleep(10000)

            dynamo.putItem(
                table, item = mapOf(
                    string to Str("hello"), number to Num(123),
                )
            ).successValue()

            val item = dynamo.getItem(
                table, mapOf(
                    string to Str("hello"),
                )
            ).successValue().item
            assertThat(item[number], equalTo(Num(123)))

            dynamo.updateItem(
                table,
                mapOf(string to Str("hello")),
                null,
                "set $number = :val1",
                expressionAttributeValues = mapOf(":val1" to Num(321))
            ).successValue()

            val updatedItem = dynamo.getItem(
                table, mapOf(
                    string to Str("hello"),
                ),
                consistentRead = true
            ).successValue().item
            assertThat(updatedItem[number], equalTo(Num(321)))

            val query = dynamo.query(table,
                keyConditionExpression = "$string = :v1",
                expressionAttributeValues = mapOf(
                    ":v1" to Str("hello")
                )
            ).successValue().items

            assertThat(query.first()[number], equalTo(Num(321)))

            dynamo.deleteItem(table, mapOf(string to Str("hello"))).successValue()

            dynamo.describeTable(table).successValue()

            dynamo.updateTable(
                table,
                billingMode = PROVISIONED,
                provisionedThroughput = ProvisionedThroughput(1, 1)
            ).successValue()
        } finally {
            dynamo.deleteTable(table)
        }
    }

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
