package org.http4k.connect.amazon.dynamodb

import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.AmazonJsonFake
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
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock
import java.time.Clock.systemUTC

class FakeDynamoDb(
    tables: Storage<DynamoTable> = Storage.InMemory(),
    private val clock: Clock = systemUTC()
) : ChaosFake() {

    private val api = AmazonJsonFake(DynamoDbMoshi, AwsService.of("DynamoDB_20120810"))

    override val app = routes(
        "/" bind POST to routes(
            api.batchExecuteStatement(tables), // todo
            api.batchGetItem(tables),
            api.batchWriteItem(tables),
            api.createTable(tables),
            api.deleteItem(tables),
            api.deleteTable(tables),
            api.describeTable(tables),
            api.executeStatement(tables), // todo
            api.executeTransaction(tables), // todo
            api.getItem(tables),
            api.listTables(tables),
            api.putItem(tables),
            api.query(tables),// todo
            api.scan(tables),// todo
            api.transactGetItems(tables),// todo
            api.transactWriteItems(tables),// todo
            api.updateItem(tables),
            api.updateTable(tables)
        )
    )

    /**
     * Convenience function to get DynamoDb client
     */
    fun client() = DynamoDb.Http(Region.of("us-east-1"), { AwsCredentials("accessKey", "secret") }, this, clock)
}

fun main() {
    FakeDynamoDb().start()
}
