package org.http4k.connect.amazon.dynamodb.mapper

import com.natpryce.hamkrest.Matcher
import org.http4k.connect.amazon.dynamodb.action.Scan
import org.http4k.connect.amazon.dynamodb.model.Key
import org.http4k.connect.amazon.dynamodb.model.TokensToNames
import org.http4k.connect.amazon.dynamodb.model.TokensToValues

private fun Scan.hasFilterExpression(expr: String?) = FilterExpression == expr
fun scanHasFilterExpression(expr: String?) = Matcher(Scan::hasFilterExpression, expr)

private fun Scan.hasAttributeNames(names: TokensToNames?) = ExpressionAttributeNames == names
fun scanHasAttributeNames(names: TokensToNames?) = Matcher(Scan::hasAttributeNames, names)

private fun Scan.hasAttributeValues(values: TokensToValues?) = ExpressionAttributeValues == values
fun scanHasAttributeValues(values: TokensToValues?) = Matcher(Scan::hasAttributeValues, values)

private fun Scan.hasLimit(limit: Int?) = Limit == limit
fun scanHasLimit(limit: Int?) = Matcher(Scan::hasLimit, limit)

private fun Scan.hasExclusiveStartKey(key: Key?) = ExclusiveStartKey == key
fun scanHasExclusiveStartKey(key: Key?) = Matcher(Scan::hasExclusiveStartKey, key)

private fun Scan.hasConsistentRead(value: Boolean?) = ConsistentRead == value
fun scanHasConsistentRead(value: Boolean?) = Matcher(Scan::hasConsistentRead, value)
