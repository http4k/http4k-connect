package org.http4k.connect.kafka.httpproxy

import org.http4k.client.JavaHttpClient
import org.http4k.connect.assumeDockerDaemonRunning
import org.http4k.core.Uri
import org.junit.jupiter.api.Disabled

@Disabled
class LocalKafkaHttpProxyTest : KafkaHttpProxyContract() {
    init {
        assumeDockerDaemonRunning()
    }

    override val uri = Uri.of("http://localhost:8082")

    override val http = JavaHttpClient()
}
