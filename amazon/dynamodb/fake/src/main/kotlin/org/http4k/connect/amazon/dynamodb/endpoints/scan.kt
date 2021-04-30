package org.http4k.connect.amazon.dynamodb.endpoints

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.dynamodb.DynamoTable
import org.http4k.connect.amazon.dynamodb.action.Scan
import org.http4k.connect.amazon.dynamodb.action.ScanResponse
import org.http4k.connect.storage.Storage

fun AmazonJsonFake.scan(tables: Storage<DynamoTable>) = route<Scan> {
    // todo
    ScanResponse()
}
