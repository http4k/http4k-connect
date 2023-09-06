package org.http4k.connect.amazon.cloudwatchlogs

import org.http4k.chaos.defaultLocalUri
import org.http4k.chaos.start
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach

class RunningFakeCloudWatchLogsTest : CloudWatchLogsContract(
    SetHostFrom(FakeCloudWatchLogs::class.defaultLocalUri).then(JavaHttpClient())
) {
    override val aws = fakeAwsEnvironment

    private lateinit var server: Http4kServer

    override fun setUp() {
        server = FakeCloudWatchLogs().start()
    }

    @AfterEach
    fun stop() {
        server.stop()
    }
}
