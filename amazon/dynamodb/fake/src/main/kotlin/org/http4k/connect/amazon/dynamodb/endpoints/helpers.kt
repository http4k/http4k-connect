package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.grammar.AttributeNameValue
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbConditionalGrammar
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbProjectionGrammar
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbUpdateExpressionParser
import org.http4k.connect.amazon.dynamodb.grammar.ItemWithSubstitutions
import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import org.http4k.connect.amazon.dynamodb.model.IndexName
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.KeySchema
import org.http4k.connect.amazon.dynamodb.model.TokensToNames
import org.http4k.connect.amazon.dynamodb.model.TokensToValues
import kotlin.Comparator

fun Item.asItemResult(): Map<String, Map<String, Any>> =
    mapKeys { it.key.value }.mapValues { convert(it.value) }

inline fun <reified OUT : Any> convert(input: Any) = DynamoDbMoshi.asA<OUT>(DynamoDbMoshi.asFormatString(input))

/**
 * Transform the input item by applying the projection to the fields in it.
 */
fun Item.project(
    projectionExpression: String?,
    expressionAttributeNames: TokensToNames?
): Item = projectionExpression?.let {
    val item = ItemWithSubstitutions(this, expressionAttributeNames ?: emptyMap())
    val allItems: List<AttributeNameValue> = it.split(',')
        .map(String::trim)
        .map(DynamoDbProjectionGrammar::parse)
        .flatMap { it.eval(item) as List<AttributeNameValue> }

    allItems
        .groupBy { it.first }
        .mapValues { it.value.map { it.second } }
        .map { (name: AttributeName, values: List<AttributeValue>) ->
            name to when {
                values[0].L != null -> AttributeValue.List(values.flatMap { it.L!! })
                values[0].M != null -> AttributeValue.Map(values
                    .map { it.M!! }
                    .fold(Item()) { acc, next -> acc + next })
                else -> values[0]
            }
        }.toMap()
} ?: this

/**
 * Apply the conditional expression to the Item. If the condition is null or resolves to true returns the item,
 * or returns null.
 */
fun Item.condition(
    expression: String?,
    expressionAttributeNames: TokensToNames?,
    expressionAttributeValues: TokensToValues?
) = when (expression) {
    null -> this
    else -> takeIf {
        DynamoDbConditionalGrammar.parse(expression).eval(
            ItemWithSubstitutions(
                this,
                expressionAttributeNames ?: emptyMap(),
                expressionAttributeValues ?: emptyMap()
            )
        ) == true
    }
}

fun Item.update(
    expression: String?,
    expressionAttributeNames: TokensToNames?,
    expressionAttributeValues: TokensToValues?
) = when (expression) {
    null -> this
    else -> DynamoDbUpdateExpressionParser.parse(expression)
        .fold(this) { i, update ->
            update.eval(
                item = i,
                names = expressionAttributeNames ?: emptyMap(),
                values = expressionAttributeValues ?: emptyMap()
            )
        }
}

fun AttributeName?.comparator(ascending: Boolean): Comparator<Item> = object: Comparator<Item> {
    val attributeName = this@comparator
    val modifier = if (ascending) 1 else -1

    override fun compare(item1: Item, item2: Item): Int {
        if (attributeName == null) return 0
        val value1 = item1[attributeName] ?: return 0
        val value2 = item2[attributeName] ?: return 0

        return value1.compareTo(value2) * modifier
    }
}

fun DynamoTable.keySchema(indexName: IndexName? = null): List<KeySchema>? {
    if (indexName == null) return table.KeySchema

    for (index in table.GlobalSecondaryIndexes ?: emptyList()) {
        if (index.IndexName == indexName.value) {
            return index.KeySchema
        }
    }

    return table.LocalSecondaryIndexes
        ?.find { it.IndexName == indexName.value }
        ?.KeySchema
}
