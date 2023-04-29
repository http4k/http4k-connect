package org.http4k.connect.amazon.dynamodb.mapper

import org.http4k.connect.amazon.dynamodb.model.*

sealed interface DynamoDbTableMapperSchema<HashKey, SortKey> {
    val hashKeyAttribute: Attribute<HashKey>
    val sortKeyAttribute: Attribute<SortKey>?
    val indexName: IndexName?

    fun keySchema() = KeySchema.compound(hashKeyAttribute.name, sortKeyAttribute?.name)
    fun attributeDefinitions() = setOfNotNull(
        hashKeyAttribute.asAttributeDefinition(),
        sortKeyAttribute?.asAttributeDefinition()
    )

    data class Primary<HashKey, SortKey>(
        override val hashKeyAttribute: Attribute<HashKey>,
        override val sortKeyAttribute: Attribute<SortKey>?
    ): DynamoDbTableMapperSchema<HashKey, SortKey> {
        companion object {
            operator fun <HashKey> invoke(hashKeyAttribute: Attribute<HashKey>) =
                Primary<HashKey, Unit>(hashKeyAttribute, null)
        }

        override val indexName = null
    }

    sealed interface Secondary<HashKey, SortKey>: DynamoDbTableMapperSchema<HashKey, SortKey>

    data class GlobalSecondary<HashKey, SortKey>(
        override val indexName: IndexName,
        override val hashKeyAttribute: Attribute<HashKey>,
        override val sortKeyAttribute: Attribute<SortKey>?,
        val projection: Projection = Projection.all
    ): Secondary<HashKey, SortKey> {
        fun toIndex() = GlobalSecondaryIndex(
            IndexName = indexName,
            KeySchema = keySchema(),
            Projection = projection
        )
    }

    data class LocalSecondary<HashKey, SortKey>(
        override val indexName: IndexName,
        override val hashKeyAttribute: Attribute<HashKey>,
        override val sortKeyAttribute: Attribute<SortKey>?,
        val projection: Projection = Projection.all
    ): Secondary<HashKey, SortKey> {
        fun toIndex() = LocalSecondaryIndex(
            IndexName = indexName,
            KeySchema = keySchema(),
            Projection = projection
        )
    }
}

fun <HashKey, SortKey> DynamoDbTableMapperSchema<HashKey, SortKey>.key(hashKey: HashKey, sortKey: SortKey?): Key {
    return if (sortKeyAttribute == null || sortKey == null) {
        Item(hashKeyAttribute of hashKey)
    } else Item(
        hashKeyAttribute of hashKey,
        sortKeyAttribute!! of sortKey
    )
}
