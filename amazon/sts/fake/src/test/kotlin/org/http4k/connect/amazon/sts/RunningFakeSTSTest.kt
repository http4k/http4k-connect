package org.http4k.connect.amazon.sts

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.defaultLocalUri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach

class RunningFakeSTSTest : STSContract(
    SetHostFrom(FakeSTS::class.defaultLocalUri).then(JavaHttpClient())
) {
    override val aws = fakeAwsEnvironment
    private lateinit var server: Http4kServer

    override fun setUp() {
        server = FakeSTS(clock).start()
    }

    @AfterEach
    fun stop() {
        server.stop()
    }
}
