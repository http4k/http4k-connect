package org.http4k.connect.amazon.dynamodb.endpoints

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import org.http4k.connect.amazon.dynamodb.AmazonDynamoFake

fun AmazonDynamoFake.updateTable(db: AmazonDynamoDB) = route(db::updateTable)

