package org.http4k.connect.amazon.dynamodb

import org.http4k.aws.AwsCredentials
import org.http4k.chaos.ChaoticHttpHandler
import org.http4k.chaos.start
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
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock

class FakeDynamoDb(private val clock: Clock = Clock.systemUTC()) : ChaoticHttpHandler() {

    init {
        System.setProperty("sqlite4java.library.path", "/Users/david/dev/http4k/http4k-connect/amazon/dynamodb/fake/lib")
    }

    private val api = AmazonDynamoFake()

    override val app = "/" bind POST to routes(
        api.batchExecuteStatement(), // todo
        api.batchGetItem(),
        api.batchWriteItem(),
        api.createTable(),
        api.deleteItem(),
        api.deleteTable(),
        api.describeTable(),
        api.executeStatement(), // todo
        api.executeTransaction(), // todo
        api.getItem(),
        api.listTables(),
        api.putItem(),
        api.query(),// todo
        api.scan(),// todo
        api.transactGetItems(),// todo
        api.transactWriteItems(),// todo
        api.updateItem(),
        api.updateTable()
    )

    /**
     * Convenience function to get DynamoDb client
     */
    fun client() = DynamoDb.Http(Region.of("us-east-1"), { AwsCredentials("accessKey", "secret") }, this, clock)
}

fun main() {
    FakeDynamoDb().start()
}
