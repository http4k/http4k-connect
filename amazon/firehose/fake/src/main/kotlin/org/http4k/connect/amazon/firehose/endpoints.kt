package org.http4k.connect.amazon.firehose

import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.firehose.action.PutRecord
import org.http4k.connect.amazon.firehose.action.PutRecordBatch
import org.http4k.connect.amazon.firehose.action.RecordAdded
import org.http4k.connect.amazon.model.Record
import org.http4k.connect.amazon.model.RequestResponses
import org.http4k.connect.storage.Storage
import java.util.UUID

fun AmazonJsonFake.putRecord(records: Storage<List<Record>>) = route<PutRecord> {
    val final = records[it.DeliveryStreamName.value] ?: listOf()
    records[it.DeliveryStreamName.value] = final + it.Record
    RecordAdded(false, UUID.randomUUID().toString())
}

fun AmazonJsonFake.putRecordBatch(records: Storage<List<Record>>) = route<PutRecordBatch> {
    val final = records[it.DeliveryStreamName.value] ?: listOf()
    records[it.DeliveryStreamName.value] = final + it.Records
    RequestResponses(null, null, UUID.randomUUID().toString())
}
