package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.dynamodb.AmazonDynamoFake
import org.http4k.connect.amazon.dynamodb.action.ExecuteStatement

fun AmazonDynamoFake.executeStatement() = route<ExecuteStatement> { null }
