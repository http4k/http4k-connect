package org.http4k.connect.amazon.dynamodb.endpoints

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import org.http4k.connect.amazon.dynamodb.AmazonDynamoFake

fun AmazonDynamoFake.scan(db: AmazonDynamoDB) = route(db::scan)
