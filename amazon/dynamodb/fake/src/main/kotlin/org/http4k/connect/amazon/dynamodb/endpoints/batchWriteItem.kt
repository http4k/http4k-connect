package org.http4k.connect.amazon.dynamodb.endpoints

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest
import org.http4k.connect.amazon.dynamodb.AmazonDynamoFake

fun AmazonDynamoFake.batchWriteItem(db: AmazonDynamoDB) = route<BatchWriteItemRequest>(db::batchWriteItem)
