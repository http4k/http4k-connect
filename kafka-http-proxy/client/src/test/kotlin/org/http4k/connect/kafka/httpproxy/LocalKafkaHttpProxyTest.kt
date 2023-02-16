package org.http4k.connect.kafka.httpproxy

import org.http4k.client.JavaHttpClient
import org.http4k.connect.assumeDockerDaemonRunning
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.debug
import org.junit.jupiter.api.Disabled

@Disabled
class LocalKafkaHttpProxyTest : KafkaHttpProxyContract() {
    init {
        assumeDockerDaemonRunning()
    }

    override val http by lazy {
        SetBaseUriFrom(Uri.of("http://localhost:8082"))
            .then(JavaHttpClient())
            .debug()
    }
}
