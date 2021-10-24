package org.http4k.connect.amazon.dynamodb.endpoints

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import org.http4k.connect.amazon.dynamodb.AmazonDynamoFake
import org.http4k.connect.amazon.dynamodb.action.ExecuteStatement

fun AmazonDynamoFake.executeStatement(db: AmazonDynamoDB) = route<ExecuteStatement> {
    null
}
