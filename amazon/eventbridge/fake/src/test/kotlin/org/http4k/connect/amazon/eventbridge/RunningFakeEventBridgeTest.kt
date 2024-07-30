package org.http4k.connect.amazon.eventbridge

import org.http4k.chaos.defaultLocalUri
import org.http4k.chaos.start
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach

class RunningFakeEventBridgeTest : EventBridgeContract(
    SetHostFrom(FakeEventBridge::class.defaultLocalUri).then(JavaHttpClient())
) {
    override val aws = fakeAwsEnvironment

    private lateinit var server: Http4kServer

    override fun setUp() {
        server = FakeEventBridge().start()
    }

    @AfterEach
    fun stop() {
        server.stop()
    }
}
