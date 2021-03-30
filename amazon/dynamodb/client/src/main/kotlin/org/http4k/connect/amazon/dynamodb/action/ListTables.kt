package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.Paged
import org.http4k.connect.amazon.PagedAction
import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.model.TableName
import se.ansman.kotshi.JsonSerializable

@Http4kConnectAction
@JsonSerializable
data class ListTables(val ExclusiveStartTableName: TableName? = null, val Limit: Int? = null) :
    DynamoDbAction<TableList>(TableList::class, DynamoDbMoshi),
    PagedAction<TableName, TableName, TableList, ListTables> {
    override fun next(token: TableName) = copy(ExclusiveStartTableName = token)
}

@JsonSerializable
data class TableList(
    val LastEvaluatedTableName: TableName?,
    val TableNames: List<TableName>
) : Paged<TableName, TableName> {
    override fun token() = LastEvaluatedTableName
    override val items = TableNames
}
