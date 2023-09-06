package org.http4k.connect.amazon.cloudwatchlogs

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.valueOrNull
import org.http4k.connect.TestClock
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.cloudwatchlogs.action.LogEvent
import org.http4k.connect.amazon.core.model.Timestamp
import org.http4k.connect.amazon.model.LogGroupName
import org.http4k.connect.amazon.model.LogStreamName
import org.http4k.core.HttpHandler
import org.http4k.filter.debug
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class CloudWatchLogsContract(http: HttpHandler) : AwsContract() {

    private val clock = TestClock()

    private val cloudWatchLogs by lazy {
        CloudWatchLogs.Http(aws.region, { aws.credentials }, http.debug())
    }

    private val logGroupName = LogGroupName.of(UUID.randomUUID().toString())
    private val logStreamName = LogStreamName.of(UUID.randomUUID().toString())

    @Test
    fun `log events lifecycle`() {
        with(cloudWatchLogs) {
            createLogGroup(logGroupName, emptyMap()).valueOrNull()!!
            createLogStream(logGroupName, logStreamName).valueOrNull()!!
            try {
                putLogEvents(
                    logGroupName, logStreamName, listOf(
                        LogEvent("hello", Timestamp.of(clock.instant())),
                        LogEvent("world", Timestamp.of(clock.instant()))
                    )
                ).valueOrNull()

                val events = filterLogEvents(logGroupName, false).valueOrNull()!!
                assertThat(events.events.size, equalTo(2))
            } catch (e: Exception) {
                deleteLogGroup(logGroupName)
                deleteLogStream(logStreamName)
            }
        }
    }
}
