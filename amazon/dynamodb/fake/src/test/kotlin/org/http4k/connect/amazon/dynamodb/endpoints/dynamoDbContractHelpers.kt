package org.http4k.connect.amazon.dynamodb.endpoints

import dev.forkhandles.result4k.Success
import org.http4k.connect.amazon.dynamodb.DynamoDb
import org.http4k.connect.amazon.dynamodb.action.DescribedTable
import org.http4k.connect.amazon.dynamodb.describeTable
import org.http4k.connect.amazon.dynamodb.model.TableName
import java.time.Duration
import java.time.Instant

fun DynamoDb.waitForExist(name: TableName, timeout: Duration = Duration.ofSeconds(10)) {
    val waitStart = Instant.now()
    while (Duration.between(waitStart, Instant.now()) < timeout) {
        if (describeTable(name) is Success<DescribedTable>) {
            return
        }
        Thread.sleep(1000)
    }
    throw IllegalStateException("Table $name was not created after $timeout")
}
