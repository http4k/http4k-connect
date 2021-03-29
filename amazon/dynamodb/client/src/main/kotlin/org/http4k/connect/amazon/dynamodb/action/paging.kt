package org.http4k.connect.amazon.dynamodb.action

import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.format.AutoMarshalling
import kotlin.reflect.KClass

interface Paged {
    val LastEvaluatedKey: Key?
    val items: List<Item>
}

abstract class PagedAction<R : Paged>(clazz: KClass<R>, autoMarshalling: AutoMarshalling = DynamoDbMoshi)
    : DynamoDbAction<R>(clazz, autoMarshalling) {
    abstract fun next(lastKey: Key): PagedAction<R>
}
