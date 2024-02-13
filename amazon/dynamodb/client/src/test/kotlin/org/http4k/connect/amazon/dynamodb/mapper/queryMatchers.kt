package org.http4k.connect.amazon.dynamodb.mapper

import com.natpryce.hamkrest.Matcher
import org.http4k.connect.amazon.dynamodb.action.Query
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.TokensToNames
import org.http4k.connect.amazon.dynamodb.model.TokensToValues

private fun Query.hasKeyConditionExpression(expr: String?) = KeyConditionExpression == expr
fun queryHasKeyConditionExpression(expr: String?) = Matcher(Query::hasKeyConditionExpression, expr)

private fun Query.hasFilterExpression(expr: String?) = FilterExpression == expr
fun queryHasFilterExpression(expr: String?) = Matcher(Query::hasFilterExpression, expr)

private fun Query.hasAttributeNames(names: TokensToNames?) = ExpressionAttributeNames == names
fun queryHasAttributeNames(names: TokensToNames?) = Matcher(Query::hasAttributeNames, names)

private fun Query.hasAttributeValues(values: TokensToValues?) = ExpressionAttributeValues == values
fun queryHasAttributeValues(values: TokensToValues?) = Matcher(Query::hasAttributeValues, values)

private fun Query.hasLimit(limit: Int?) = Limit == limit
fun queryHasLimit(limit: Int?) = Matcher(Query::hasLimit, limit)

private fun Query.hasExclusiveStartKey(key: Key?) = ExclusiveStartKey == key
fun queryHasExclusiveStartKey(key: Key?) = Matcher(Query::hasExclusiveStartKey, key)

private fun Query.hasConsistentRead(value: Boolean?) = ConsistentRead == value
fun queryHasConsistentRead(value: Boolean?) = Matcher(Query::hasConsistentRead, value)

private fun Query.hasScanIndexForward(value: Boolean?) = ScanIndexForward == value
fun queryHasScanIndexForward(value: Boolean?) = Matcher(Query::hasScanIndexForward, value)
