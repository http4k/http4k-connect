package org.http4k.connect.kafka.rest

import http4k.RandomEvent
import org.http4k.connect.kafka.rest.KafkaRestMoshi.asFormatString
import org.http4k.connect.kafka.rest.model.Record
import org.http4k.connect.kafka.rest.model.Records
import org.http4k.connect.model.Base64Blob
import org.http4k.testing.ApprovalTest
import org.http4k.testing.Approver
import org.http4k.testing.assertApproved
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID

@ExtendWith(ApprovalTest::class)
class KafkaRestMoshiTest {

    @Test
    fun `can serialise avro records`(approver: Approver) {
        approver.assertApproved(asFormatString(Records.Avro(listOf(Record("123", RandomEvent(UUID(0, 0)))))))
    }

    @Test
    fun `can serialise json records`(approver: Approver) {
        approver.assertApproved(asFormatString(Records.Json(listOf(Record(123, Message("123"))))))
    }

    @Test
    fun `can serialise binary records`(approver: Approver) {
        approver.assertApproved(asFormatString(Records.Binary(listOf(Record(Base64Blob.encode("123"), Base64Blob.encode("456"))))))
    }
}
