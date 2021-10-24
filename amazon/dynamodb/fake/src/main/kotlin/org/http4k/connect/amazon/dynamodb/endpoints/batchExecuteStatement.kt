package org.http4k.connect.amazon.dynamodb.endpoints

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import org.http4k.connect.amazon.dynamodb.AmazonDynamoFake
import org.http4k.connect.amazon.dynamodb.action.BatchExecuteStatement

fun AmazonDynamoFake.batchExecuteStatement(db: AmazonDynamoDB) = route<BatchExecuteStatement> {
    null
}

