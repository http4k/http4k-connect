package org.http4k.connect.amazon.dynamodb.mapper

import org.http4k.connect.amazon.dynamodb.model.Attribute
import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.AttributeValue

internal class DynamoDbQuery(
    val keyConditionExpression: String?,
    val filterExpression: String?,
    val expressionAttributeNames: Map<String, AttributeName>?,
    val expressionAttributeValues: Map<String, AttributeValue>?,
    val scanIndexForward: Boolean,
    val pageSize: Int?,
    val consistentRead: Boolean?
)

interface KeyCondition {
    val expression: String
    val attributeNames: Map<String, AttributeName>
    val attributeValues: Map<String, AttributeValue>
}

interface SortKeyCondition : KeyCondition
interface CombinedKeyCondition : KeyCondition
interface PartitionKeyCondition : SortKeyCondition, CombinedKeyCondition

class FilterExpression(
    val expression: String,
    val attributeNames: Map<String, AttributeName>,
    val attributeValues: Map<String, AttributeValue>
)

class DynamoDbQueryBuilder {

    /**
     * See https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Query.KeyConditionExpressions.html
     */
    inner class KeyConditionBuilder {

        infix fun <T> Attribute<T>.eq(value: T) = nextAttributeName().let { attributeName ->
            object : PartitionKeyCondition {
                override val expression = "#$attributeName = :$attributeName"
                override val attributeNames = mapOf("#$attributeName" to name)
                override val attributeValues = mapOf(":$attributeName" to asValue(value))
            }
        }

        private fun sortKeyCondition(
            expr: String,
            attrNames: Map<String, AttributeName>,
            attrValues: Map<String, AttributeValue>
        ) = object : SortKeyCondition {
            override val expression = expr
            override val attributeNames = attrNames
            override val attributeValues = attrValues
        }

        private fun <T> Attribute<T>.sortKeyOperator(op: String, value: T) = nextAttributeName().let { attributeName ->
            sortKeyCondition(
                "#$attributeName $op :$attributeName",
                mapOf("#$attributeName" to name),
                mapOf(":$attributeName" to asValue(value))
            )
        }

        infix fun <T> Attribute<T>.lt(value: T) = sortKeyOperator("<", value)
        infix fun <T> Attribute<T>.le(value: T) = sortKeyOperator("<=", value)
        infix fun <T> Attribute<T>.gt(value: T) = sortKeyOperator(">", value)
        infix fun <T> Attribute<T>.ge(value: T) = sortKeyOperator(">=", value)

        fun <T> between(attr: Attribute<T>, value1: T, value2: T) = nextAttributeName().let { attributeName ->
            sortKeyCondition(
                "#$attributeName BETWEEN :${attributeName}1 AND :${attributeName}2",
                mapOf("#$attributeName" to attr.name),
                mapOf(":${attributeName}1" to attr.asValue(value1), ":${attributeName}2" to attr.asValue(value2))
            )
        }

        infix fun <T> Attribute<T>.beginsWith(value: T) = nextAttributeName().let { attributeName ->
            sortKeyCondition(
                "begins_with(#$attributeName,:$attributeName)",
                mapOf("#$attributeName" to name),
                mapOf(":$attributeName" to asValue(value))
            )
        }

        infix fun PartitionKeyCondition.and(secondary: SortKeyCondition?): CombinedKeyCondition = let {
            if (secondary == null) {
                this
            } else {
                object : CombinedKeyCondition {
                    override val expression = "${it.expression} AND ${secondary.expression}"
                    override val attributeNames = it.attributeNames + secondary.attributeNames
                    override val attributeValues = it.attributeValues + secondary.attributeValues
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

    private var keyCondition: KeyCondition? = null
    private var filterExpression: FilterExpression? = null

    fun keyCondition(block: KeyConditionBuilder.() -> CombinedKeyCondition) {
        keyCondition = block(KeyConditionBuilder())
    }

    fun filterExpression(block: FilterExpressionBuilder.() -> FilterExpression?) {
        filterExpression = block(FilterExpressionBuilder())
    }

    var scanIndexForward: Boolean = true
    var pageSize: Int? = null
    var consistentRead: Boolean? = null

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
        scanIndexForward = scanIndexForward,
        pageSize = pageSize,
        consistentRead = consistentRead
    )
}

fun <Document : Any, HashKey : Any, SortKey : Any> DynamoDbIndexMapper<Document, HashKey, SortKey>.query(block: DynamoDbQueryBuilder.() -> Unit): Sequence<Document> {
    val query = DynamoDbQueryBuilder().apply(block).build()
    return query(
        KeyConditionExpression = query.keyConditionExpression,
        FilterExpression = query.filterExpression,
        ExpressionAttributeNames = query.expressionAttributeNames,
        ExpressionAttributeValues = query.expressionAttributeValues,
        ScanIndexForward = query.scanIndexForward,
        PageSize = query.pageSize,
        ConsistentRead = query.consistentRead
    )
}

fun <Document : Any, HashKey : Any, SortKey : Any> DynamoDbIndexMapper<Document, HashKey, SortKey>.queryPage(
    exclusiveStartHashKey: HashKey? = null,
    exclusiveStartSortKey: SortKey? = null,
    block: DynamoDbQueryBuilder.() -> Unit
): DynamoDbPage<Document, HashKey, SortKey> {
    val query = DynamoDbQueryBuilder().apply(block).build()
    return queryPage(
        KeyConditionExpression = query.keyConditionExpression,
        FilterExpression = query.filterExpression,
        ExpressionAttributeNames = query.expressionAttributeNames,
        ExpressionAttributeValues = query.expressionAttributeValues,
        ExclusiveStartHashKey = exclusiveStartHashKey,
        ExclusiveStartSortKey = exclusiveStartSortKey,
        ScanIndexForward = query.scanIndexForward,
        Limit = query.pageSize,
        ConsistentRead = query.consistentRead
    )
}
