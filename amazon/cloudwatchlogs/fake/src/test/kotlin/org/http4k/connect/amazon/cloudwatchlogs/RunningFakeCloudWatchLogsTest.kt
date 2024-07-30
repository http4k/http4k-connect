package org.http4k.connect.amazon.cloudwatchlogs

import org.http4k.chaos.defaultLocalUri
import org.http4k.chaos.start
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.FakeAwsContract
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

class RunningFakeCloudWatchLogsTest : CloudWatchLogsContract, FakeAwsContract {

    override val http = SetHostFrom(FakeCloudWatchLogs::class.defaultLocalUri).then(JavaHttpClient())
    private lateinit var server: Http4kServer

    @BeforeEach
    fun setUp() {
        server = FakeCloudWatchLogs().start()
    }

    @AfterEach
    fun stop() {
        server.stop()
    }
}
