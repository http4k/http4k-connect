package org.http4k.connect.kafka.httpproxy

import org.http4k.core.Uri

class FakeKafkaHttpProxyTest : KafkaHttpProxyContract() {
    override val http = FakeKafkaHttpProxy()
    override val uri = Uri.of("http://proxy")
}
