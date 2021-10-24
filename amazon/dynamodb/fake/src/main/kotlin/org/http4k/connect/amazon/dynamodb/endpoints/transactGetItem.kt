package org.http4k.connect.amazon.dynamodb.endpoints

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import org.http4k.connect.amazon.dynamodb.AmazonDynamoFake

fun AmazonDynamoFake.transactGetItems(db: AmazonDynamoDB) = route(db::transactGetItems)
