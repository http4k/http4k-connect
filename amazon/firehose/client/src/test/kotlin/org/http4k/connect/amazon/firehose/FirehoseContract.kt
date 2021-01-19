package org.http4k.connect.amazon.firehose

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.firehose.action.Record
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.DeliveryStreamName
import org.http4k.connect.randomString
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Test

abstract class FirehoseContract(http: HttpHandler) : AwsContract(http) {
    private val firehose by lazy {
        Firehose.Http(aws.region, { aws.credentials }, http)
    }

    private val deliveryStreamName = DeliveryStreamName.of("connect")

    @Test
    fun `send records`() {
        with(firehose) {
            assertThat(listDeliveryStreams().successValue().DeliveryStreamNames.contains(deliveryStreamName), equalTo(true))
            putRecord(
                deliveryStreamName,
                Record(Base64Blob.encoded(randomString))
            ).successValue()

            putRecordBatch(
                deliveryStreamName,
                listOf(Record(Base64Blob.encoded(randomString)))
            ).successValue()
        }
    }
}
