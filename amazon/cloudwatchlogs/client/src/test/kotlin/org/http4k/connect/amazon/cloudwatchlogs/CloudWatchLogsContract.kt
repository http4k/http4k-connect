package org.http4k.connect.amazon.cloudwatchlogs

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.allValues
import dev.forkhandles.result4k.valueOrNull
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.cloudwatchlogs.action.LogEvent
import org.http4k.connect.amazon.cloudwatchlogs.model.LogGroupName
import org.http4k.connect.amazon.cloudwatchlogs.model.LogStreamName
import org.http4k.connect.amazon.core.model.TimestampMillis
import org.http4k.core.HttpHandler
import org.http4k.filter.debug
import org.junit.jupiter.api.Test
import java.time.Clock
import java.util.UUID

abstract class CloudWatchLogsContract : AwsContract {

    abstract val http: HttpHandler

    private val clock = Clock.systemUTC()

    private val cloudWatchLogs by lazy {
        CloudWatchLogs.Http(aws.region, { aws.credentials }, http.debug())
    }

    private val logGroupName = LogGroupName.of(UUID.randomUUID().toString())
    private val logStreamName = LogStreamName.of(UUID.randomUUID().toString())

    @Test
    fun `log events lifecycle`() {
        with(cloudWatchLogs) {
            createLogGroup(logGroupName, mapOf("1" to "2")).valueOrNull()!!
            createLogStream(logGroupName, logStreamName).valueOrNull()!!
            try {
                putLogEvents(
                    logGroupName, logStreamName, listOf(
                        LogEvent("hello", TimestampMillis.of(clock.instant())),
                        LogEvent("world", TimestampMillis.of(clock.instant()))
                    )
                ).valueOrNull()

                Thread.sleep(2000)

                val eventResults = filterLogEventsPaginated(
                    logGroupName, false,
                    logStreamNames = listOf(logStreamName),
                    limit = 1
                ).take(2).toList()
                val result = eventResults.allValues().valueOrNull()!!.flatten()
                assertThat(result.size, equalTo(2))
            } catch (e: Exception) {
                deleteLogGroup(logGroupName)
                deleteLogStream(logGroupName, logStreamName)
            }
        }
    }
}
