package org.http4k.connect.kafka.httpproxy

class FakeKafkaHttpProxyTest : KafkaHttpProxyContract() {
    override val http = FakeKafkaHttpProxy()
}
