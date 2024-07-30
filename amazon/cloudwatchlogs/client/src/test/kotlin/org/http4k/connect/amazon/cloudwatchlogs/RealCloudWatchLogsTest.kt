package org.http4k.connect.amazon.cloudwatchlogs

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment

class RealCloudWatchLogsTest : CloudWatchLogsContract(), RealAwsEnvironment {
    override val http = JavaHttpClient()
    override val aws get() = configAwsEnvironment()
}
