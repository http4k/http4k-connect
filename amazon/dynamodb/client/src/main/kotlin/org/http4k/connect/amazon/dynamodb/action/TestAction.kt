package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.Paged
import org.http4k.connect.PagedAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.model.TableName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class TestAction(
    val TableName: TableName
) : DynamoDbAction<TestResponse>(TestResponse::class, DynamoDbMoshi),
    PagedAction<Key, Item, TestResponse, TestAction> {
    override fun next(token: Key) = copy()
}

@JsonSerializable
data class TestResponse(
    val ConsumedCapacity: ConsumedCapacity?,
) : Paged<Key, Item> {
    override val items = emptyList<Item>()
    override fun token() = Key()
}
