package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.format.AutoMarshalling
import kotlin.reflect.KClass

/**
 * Represents a Paged response
 */
interface Paged<Token, ItemType> {
    fun token(): Token?
    val items: List<ItemType>
}

/**
 * Superclass for all Paged actions
 */
abstract class PagedAction<Token, ItemType, Rsp : Paged<Token, ItemType>>(clazz: KClass<Rsp>, autoMarshalling: AutoMarshalling = DynamoDbMoshi)
    : DynamoDbAction<Rsp>(clazz, autoMarshalling) {
    abstract fun next(token: Token): PagedAction<Token ,ItemType, Rsp>
}
