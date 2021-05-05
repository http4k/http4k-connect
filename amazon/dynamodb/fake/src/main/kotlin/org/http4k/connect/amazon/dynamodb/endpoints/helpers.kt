package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.dynamodb.DynamoDbMoshi
import org.http4k.connect.amazon.dynamodb.grammar.AttributeNameValue
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbConditionalGrammar
import org.http4k.connect.amazon.dynamodb.grammar.DynamoDbProjectionGrammar
import org.http4k.connect.amazon.dynamodb.grammar.ItemWithSubstitutions
import org.http4k.connect.amazon.dynamodb.model.AttributeName
import org.http4k.connect.amazon.dynamodb.model.AttributeValue
import org.http4k.connect.amazon.dynamodb.model.Item
import org.http4k.connect.amazon.dynamodb.model.TokensToNames
import org.http4k.connect.amazon.dynamodb.model.TokensToValues

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
