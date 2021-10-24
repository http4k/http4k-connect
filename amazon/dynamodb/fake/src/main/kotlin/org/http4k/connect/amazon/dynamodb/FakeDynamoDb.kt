package org.http4k.connect.amazon.dynamodb

import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded
import com.amazonaws.services.dynamodbv2.local.shared.mapper.DynamoDBObjectMapper
import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.aws.AwsCredentials
import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.start
import org.http4k.connect.amazon.core.model.AwsService
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.dynamodb.endpoints.batchExecuteStatement
import org.http4k.connect.amazon.dynamodb.endpoints.batchGetItem
import org.http4k.connect.amazon.dynamodb.endpoints.batchWriteItem
import org.http4k.connect.amazon.dynamodb.endpoints.createTable
import org.http4k.connect.amazon.dynamodb.endpoints.deleteItem
import org.http4k.connect.amazon.dynamodb.endpoints.deleteTable
import org.http4k.connect.amazon.dynamodb.endpoints.describeTable
import org.http4k.connect.amazon.dynamodb.endpoints.executeStatement
import org.http4k.connect.amazon.dynamodb.endpoints.executeTransaction
import org.http4k.connect.amazon.dynamodb.endpoints.getItem
import org.http4k.connect.amazon.dynamodb.endpoints.listTables
import org.http4k.connect.amazon.dynamodb.endpoints.putItem
import org.http4k.connect.amazon.dynamodb.endpoints.query
import org.http4k.connect.amazon.dynamodb.endpoints.scan
import org.http4k.connect.amazon.dynamodb.endpoints.transactGetItems
import org.http4k.connect.amazon.dynamodb.endpoints.transactWriteItems
import org.http4k.connect.amazon.dynamodb.endpoints.updateItem
import org.http4k.connect.amazon.dynamodb.endpoints.updateTable
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.header
import org.http4k.routing.routes
import java.time.Clock

class AmazonDynamoFake(val mapper: ObjectMapper, val awsService: AwsService) {
    inline fun <reified Req : Any> route(crossinline fn: (Req) -> Any?) =
        header("X-Amz-Target", "${awsService}.${Req::class.simpleName!!.removeSuffix("Request")}") bind {
            fn(mapper.readValue(it.bodyString(), Req::class.java))
                ?.let { Response(Status.OK).body(mapper.writeValueAsString(it)) }
                ?: Response(Status.BAD_REQUEST)
                    .body(
                        mapper.writeValueAsString(
                            JsonError(
                                "ResourceNotFoundException",
                                "$awsService can't find the specified item."
                            )
                        )
                    )
        }
}

data class JsonError(val __type: String, val Message: String)

class FakeDynamoDb(private val clock: Clock = Clock.systemUTC()) : ChaoticHttpHandler() {

    private val embedded = DynamoDBEmbedded.create().amazonDynamoDB()

    private val api = AmazonDynamoFake(DynamoDBObjectMapper(), AwsService.of("DynamoDB_20120810"))

    override val app = "/" bind POST to routes(
        api.batchExecuteStatement(embedded), // todo
        api.batchGetItem(embedded),
        api.batchWriteItem(embedded),
        api.createTable(embedded),
        api.deleteItem(embedded),
        api.deleteTable(embedded),
        api.describeTable(embedded),
        api.executeStatement(embedded), // todo
        api.executeTransaction(embedded), // todo
        api.getItem(embedded),
        api.listTables(embedded),
        api.putItem(embedded),
        api.query(embedded),// todo
        api.scan(embedded),// todo
        api.transactGetItems(embedded),// todo
        api.transactWriteItems(embedded),// todo
        api.updateItem(embedded),
        api.updateTable(embedded)
    )

    /**
     * Convenience function to get DynamoDb client
     */
    fun client() = DynamoDb.Http(Region.of("us-east-1"), { AwsCredentials("accessKey", "secret") }, this, clock)
}

fun main() {
    FakeDynamoDb().start()
}
