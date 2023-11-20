package org.http4k.connect.amazon.dynamodb.mapper

import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.AttributeValue

internal class DynamoDbQuery<HashKey: Any>(
    val keyConditionExpression: String?,
    val filterExpression: String?,
    val expressionAttributeNames: Map<String, AttributeName>?,
    val expressionAttributeValues: Map<String, AttributeValue>?,
    val exclusiveStartHashKey: HashKey?
)

interface KeyCondition<HashKey: Any, SortKey: Any> {
    val expression: String
    val attributeNames: Map<String, AttributeName>
    val attributeValues: Map<String, AttributeValue>
    val exclusiveStartHashKey: HashKey?
}

interface SortKeyCondition<HashKey: Any, SortKey: Any> : KeyCondition<HashKey, SortKey>
interface CombinedKeyCondition<HashKey: Any, SortKey: Any> : KeyCondition<HashKey, SortKey>
interface PartitionKeyCondition<HashKey: Any, SortKey: Any> : SortKeyCondition<HashKey, SortKey>, CombinedKeyCondition<HashKey, SortKey>

class FilterExpression(
    val expression: String,
    val attributeNames: Map<String, AttributeName>,
    val attributeValues: Map<String, AttributeValue>
)

class DynamoDbQueryBuilder<HashKey : Any, SortKey : Any> {

    /**
     * See https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Query.KeyConditionExpressions.html
     */
    inner class KeyConditionBuilder {

        infix fun Attribute<HashKey>.eq(value: HashKey) = nextAttributeName().let { attributeName ->
            object : PartitionKeyCondition<HashKey, SortKey> {
                override val expression = "#$attributeName = :$attributeName"
                override val attributeNames = mapOf("#$attributeName" to name)
                override val attributeValues = mapOf(":$attributeName" to asValue(value))
                override val exclusiveStartHashKey = value
            }
        }

        private fun sortKeyCondition(
            expr: String,
            attrNames: Map<String, AttributeName>,
            attrValues: Map<String, AttributeValue>
        ) = object : SortKeyCondition<HashKey, SortKey> {
            override val expression = expr
            override val attributeNames = attrNames
            override val attributeValues = attrValues
            override val exclusiveStartHashKey = null
        }

        private fun Attribute<SortKey>.sortKeyOperator(op: String, value: SortKey) = nextAttributeName().let { attributeName ->
            sortKeyCondition(
                "#$attributeName $op :$attributeName",
                mapOf("#$attributeName" to name),
                mapOf(":$attributeName" to asValue(value))
            )
        }

        infix fun Attribute<SortKey>.lt(value: SortKey) = sortKeyOperator("<", value)
        infix fun Attribute<SortKey>.le(value: SortKey) = sortKeyOperator("<=", value)
        infix fun Attribute<SortKey>.gt(value: SortKey) = sortKeyOperator(">", value)
        infix fun Attribute<SortKey>.ge(value: SortKey) = sortKeyOperator(">=", value)

        fun between(attr: Attribute<SortKey>, value1: SortKey, value2: SortKey) = nextAttributeName().let { attributeName ->
            sortKeyCondition(
                "#$attributeName BETWEEN :${attributeName}1 AND :${attributeName}2",
                mapOf("#$attributeName" to attr.name),
                mapOf(":${attributeName}1" to attr.asValue(value1), ":${attributeName}2" to attr.asValue(value2))
            )
        }

        infix fun Attribute<SortKey>.beginsWith(value: SortKey) = nextAttributeName().let { attributeName ->
            sortKeyCondition(
                "begins_with(#$attributeName,:$attributeName)",
                mapOf("#$attributeName" to name),
                mapOf(":$attributeName" to asValue(value))
            )
        }

        infix fun PartitionKeyCondition<HashKey, SortKey>.and(secondary: SortKeyCondition<HashKey, SortKey>?): CombinedKeyCondition<HashKey, SortKey> = let {
            if (secondary == null) {
                this
            } else {
                object : CombinedKeyCondition<HashKey, SortKey> {
                    override val expression = "${it.expression} AND ${secondary.expression}"
                    override val attributeNames = it.attributeNames + secondary.attributeNames
                    override val attributeValues = it.attributeValues + secondary.attributeValues
                    override val exclusiveStartHashKey = it.exclusiveStartHashKey
                }
            }
        }
    }

    /**
     * See https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Expressions.OperatorsAndFunctions.html#Expressions.OperatorsAndFunctions.Syntax
     */
    inner class FilterExpressionBuilder {

        private fun <T> Attribute<T>.filterOperator(op: String, value: T) = nextAttributeName().let { attributeName ->
            FilterExpression(
                "#$attributeName $op :$attributeName",
                mapOf("#$attributeName" to name),
                mapOf(":$attributeName" to asValue(value))
            )
        }

        infix fun <T> Attribute<T>.eq(value: T) = filterOperator("=", value)
        infix fun <T> Attribute<T>.ne(value: T) = filterOperator("<>", value)
        infix fun <T> Attribute<T>.lt(value: T) = filterOperator("<", value)
        infix fun <T> Attribute<T>.le(value: T) = filterOperator("<=", value)
        infix fun <T> Attribute<T>.gt(value: T) = filterOperator(">", value)
        infix fun <T> Attribute<T>.ge(value: T) = filterOperator(">=", value)

        fun <T> between(attr: Attribute<T>, value1: T, value2: T) = nextAttributeName().let { attributeName ->
            FilterExpression(
                "#$attributeName BETWEEN :${attributeName}1 AND :${attributeName}2",
                mapOf("#$attributeName" to attr.name),
                mapOf(":${attributeName}1" to attr.asValue(value1), ":${attributeName}2" to attr.asValue(value2))
            )
        }

        infix fun <T> Attribute<T>.`in`(values: Iterable<T>): FilterExpression {
            val attributeName = nextAttributeName()
            val attributeValues = mutableMapOf<String, AttributeValue>()
            val expression = StringBuilder("#$attributeName IN (")
            values.iterator().withIndex().forEach { (index, value) ->
                val valueName = ":$attributeName$index"
                attributeValues[valueName] = asValue(value)
                if (index > 0) {
                    expression.append(',')
                }
                expression.append(valueName)
            }
            expression.append(')')

            require(attributeValues.isNotEmpty()) { "IN operator requires at least one element" }
            return FilterExpression(expression.toString(), mapOf("#$attributeName" to name), attributeValues)
        }

        fun attributeExists(attr: Attribute<*>): FilterExpression = nextAttributeName().let { attributeName ->
            FilterExpression(
                "attribute_exists(#$attributeName)",
                mapOf("#$attributeName" to attr.name),
                emptyMap()
            )
        }

        fun attributeNotExists(attr: Attribute<*>): FilterExpression = nextAttributeName().let { attributeName ->
            FilterExpression(
                "attribute_not_exists(#$attributeName)",
                mapOf("#$attributeName" to attr.name),
                emptyMap()
            )
        }

        infix fun <T> Attribute<T>.beginsWith(value: T) = nextAttributeName().let { attributeName ->
            FilterExpression(
                "begins_with(#$attributeName,:$attributeName)",
                mapOf("#$attributeName" to name),
                mapOf(":$attributeName" to asValue(value))
            )
        }

        infix fun <T> Attribute<T>.contains(value: T) = nextAttributeName().let { attributeName ->
            FilterExpression(
                "contains(#$attributeName,:$attributeName)",
                mapOf("#$attributeName" to name),
                mapOf(":$attributeName" to asValue(value))
            )
        }

        infix fun FilterExpression?.and(other: FilterExpression?): FilterExpression? = when {
            this == null -> other
            other == null -> this
            else -> FilterExpression(
                "($expression AND ${other.expression})",
                attributeNames + other.attributeNames,
                attributeValues + other.attributeValues
            )
        }

        infix fun FilterExpression?.or(other: FilterExpression?): FilterExpression? = when {
            this == null -> other
            other == null -> this
            else -> FilterExpression(
                "($expression OR ${other.expression})",
                attributeNames + other.attributeNames,
                attributeValues + other.attributeValues
            )
        }

        fun not(expr: FilterExpression?): FilterExpression? = expr?.let {
            FilterExpression(
                "(NOT ${it.expression})",
                it.attributeNames,
                it.attributeValues
            )
        }
    }

    private var attributeNameCount = 0

    // generate consecutive names "a", "b", ... to be used as expression attribute name
    private fun nextAttributeName(): String {
        val base = 'z' - 'a' + 1
        val name = StringBuilder()
        var currentCount = attributeNameCount
        do {
            val remainder = currentCount % base
            currentCount /= base
            name.insert(0, 'a' + remainder)
        } while (currentCount > 0)

        attributeNameCount += 1
        return name.toString()
    }

    private var keyCondition: KeyCondition<HashKey, SortKey>? = null
    private var filterExpression: FilterExpression? = null

    fun keyCondition(block: KeyConditionBuilder.() -> CombinedKeyCondition<HashKey, SortKey>) {
        keyCondition = block(KeyConditionBuilder())
    }

    fun filterExpression(block: FilterExpressionBuilder.() -> FilterExpression?) {
        filterExpression = block(FilterExpressionBuilder())
    }

    private fun <K, V> union(map1: Map<K, V>?, map2: Map<K, V>?) = when {
        map1 == null -> map2
        map2 == null -> map1
        else -> map1 + map2
    }

    internal fun build() = DynamoDbQuery(
        keyConditionExpression = keyCondition?.expression,
        filterExpression = filterExpression?.expression,
        expressionAttributeNames = union(keyCondition?.attributeNames, filterExpression?.attributeNames),
        expressionAttributeValues = union(keyCondition?.attributeValues, filterExpression?.attributeValues),
        exclusiveStartHashKey = keyCondition?.exclusiveStartHashKey
    )
}

fun <Document : Any, HashKey : Any, SortKey : Any> DynamoDbIndexMapper<Document, HashKey, SortKey>.query(
    ScanIndexForward: Boolean = true,
    PageSize: Int? = null,
    ConsistentRead: Boolean? = null,
    block: DynamoDbQueryBuilder<HashKey, SortKey>.() -> Unit): Sequence<Document> {
    val query = DynamoDbQueryBuilder<HashKey, SortKey>().apply(block).build()
    return query(
        KeyConditionExpression = query.keyConditionExpression,
        FilterExpression = query.filterExpression,
        ExpressionAttributeNames = query.expressionAttributeNames,
        ExpressionAttributeValues = query.expressionAttributeValues,
        ScanIndexForward = ScanIndexForward,
        PageSize = PageSize,
        ConsistentRead = ConsistentRead
    )
}

fun <Document : Any, HashKey : Any, SortKey : Any> DynamoDbIndexMapper<Document, HashKey, SortKey>.queryPage(
    ScanIndexForward: Boolean = true,
    Limit: Int? = null,
    ConsistentRead: Boolean? = null,
    ExclusiveStartKey: SortKey? = null,
    block: DynamoDbQueryBuilder<HashKey, SortKey>.() -> Unit
): DynamoDbPage<Document, HashKey, SortKey> {
    val query = DynamoDbQueryBuilder<HashKey, SortKey>().apply(block).build()
    return queryPage(
        KeyConditionExpression = query.keyConditionExpression,
        FilterExpression = query.filterExpression,
        ExpressionAttributeNames = query.expressionAttributeNames,
        ExpressionAttributeValues = query.expressionAttributeValues,
        ExclusiveStartHashKey = query.exclusiveStartHashKey,
        ExclusiveStartSortKey = ExclusiveStartKey,
        ScanIndexForward = ScanIndexForward,
        Limit = Limit,
        ConsistentRead = ConsistentRead
    )
}
