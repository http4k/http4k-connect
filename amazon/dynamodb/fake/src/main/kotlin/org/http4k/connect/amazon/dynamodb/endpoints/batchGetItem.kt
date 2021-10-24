package org.http4k.connect.amazon.dynamodb.endpoints

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.BatchGetItemRequest
import org.http4k.connect.amazon.dynamodb.AmazonDynamoFake

fun AmazonDynamoFake.batchGetItem() = route<BatchGetItemRequest>(AmazonDynamoDB::batchGetItem)
