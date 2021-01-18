package org.http4k.connect.amazon.firehose

import org.http4k.connect.SystemMoshiContract
import org.http4k.connect.amazon.firehose.action.PutRecord
import org.http4k.connect.amazon.firehose.action.PutRecordBatch
import org.http4k.connect.amazon.firehose.action.Record
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.DeliveryStreamName
import org.http4k.connect.randomString

val Blob = Base64Blob.encoded(randomString)

val Record = Record(Blob)

class FirehoseMoshiTest : SystemMoshiContract(
    FirehoseMoshi,
    PutRecord(DeliveryStreamName.of("123"), Record),
    PutRecordBatch(DeliveryStreamName.of("123"), listOf(Record)),
)
