package org.http4k.connect.amazon.dynamodb

import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.core.model.AwsService
import org.http4k.connect.amazon.core.model.Region
import org.http4k.connect.amazon.dynamodb.endpoints.createTable
import org.http4k.connect.amazon.dynamodb.endpoints.deleteTable
import org.http4k.connect.amazon.dynamodb.endpoints.getItem
import org.http4k.connect.amazon.dynamodb.endpoints.listTables
import org.http4k.connect.amazon.dynamodb.endpoints.putItem
import org.http4k.connect.amazon.dynamodb.endpoints.updateItem
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.TableDescription
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock
import java.time.Clock.systemUTC

data class TableDefinition(val table: TableDescription, val items: List<Item>) {
    fun retrieve(key: Key) = items.firstOrNull { it.toList().containsAll(key.toList()) }

    fun withItem(item: Item) = copy(items = items + item)
}

class FakeDynamoDb(
    tables: Storage<TableDefinition> = Storage.InMemory(),
    private val clock: Clock = systemUTC()
) : ChaosFake() {

    private val api = AmazonJsonFake(DynamoDbMoshi, AwsService.of("DynamoDB_20120810"))

    override val app = routes(
        "/" bind POST to routes(
            api.createTable(tables),
            api.deleteTable(tables),
            api.getItem(tables),
            api.listTables(tables),
            api.putItem(tables),
            api.updateItem(tables)
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
