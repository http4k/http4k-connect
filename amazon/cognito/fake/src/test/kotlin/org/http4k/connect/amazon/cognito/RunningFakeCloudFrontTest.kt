package org.http4k.connect.amazon.cloudfront

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach

class RunningFakeCloudFrontTest : CloudFrontContract(
    SetHostFrom(FakeCloudFront::class.defaultLocalUri).then(JavaHttpClient())
) {
    override val aws = fakeAwsEnvironment
    private lateinit var server: Http4kServer

    override fun setUp() {
        server = FakeCloudFront().start()
    }

    @AfterEach
    fun stop() {
        server.stop()
    }
}
