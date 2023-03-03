package org.http4k.connect.kafka.rest

import org.http4k.core.Uri

class FakeKafkaRestTest : KafkaRestContract() {
    override val http = FakeKafkaRest()
    override val uri = Uri.of("http://proxy")
}
