package org.http4k.connect.amazon.firehose

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.DeliveryStreamType.DirectPut
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.core.model.Base64Blob
import org.http4k.connect.amazon.model.DeliveryStreamName
import org.http4k.connect.amazon.model.Record
import org.http4k.connect.amazon.model.S3DestinationConfiguration
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class FirehoseContract(http: HttpHandler) : AwsContract() {

    private val firehose by lazy {
        Firehose.Http(aws.region, { aws.credentials }, http)
    }

    private val deliveryStreamName = DeliveryStreamName.of("connect")

    @Test
    fun `create and delete delivery stream`() {
        val deliveryStreamName = DeliveryStreamName.of(UUID.randomUUID().toString())
        with(firehose) {
            try {
                createDeliveryStream(
                    deliveryStreamName, DirectPut,
                    S3DestinationConfiguration = S3DestinationConfiguration(
                        BucketARN = ARN.of(""),
                        RoleARN = ARN.of("")
                    )
                ).successValue()
                assertThat(
                    listDeliveryStreams().successValue().DeliveryStreamNames.contains(deliveryStreamName),
                    equalTo(true)
                )
            } finally {
                deleteDeliveryStream(deliveryStreamName).successValue()
            }
        }
    }

    @Test
    @Disabled
    fun `send records`() {
        with(firehose) {
            assertThat(
                listDeliveryStreams().successValue().DeliveryStreamNames.contains(deliveryStreamName),
                equalTo(true)
            )
            putRecord(
                deliveryStreamName,
                Record(Base64Blob.encode(UUID.randomUUID().toString()))
            ).successValue()

            putRecordBatch(
                deliveryStreamName,
                listOf(Record(Base64Blob.encode(UUID.randomUUID().toString())))
            ).successValue()
        }
    }
}
