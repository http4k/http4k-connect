package org.http4k.connect.amazon.dynamodb.endpoints

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.ListTablesRequest
import org.http4k.connect.amazon.dynamodb.AmazonDynamoFake

fun AmazonDynamoFake.listTables(db: AmazonDynamoDB) = route<ListTablesRequest> { db.listTables() }
