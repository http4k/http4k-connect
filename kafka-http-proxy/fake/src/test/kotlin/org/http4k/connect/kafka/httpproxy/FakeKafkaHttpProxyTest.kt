package org.http4k.connect.kafka.httpproxy

import org.http4k.core.Uri
import org.http4k.filter.debug

class FakeKafkaHttpProxyTest : KafkaHttpProxyContract() {
    override val http = FakeKafkaHttpProxy().debug()
    override val uri = Uri.of("http://proxy")
}
