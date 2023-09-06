package org.http4k.connect.amazon.cloudwatchlogs

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeCloudWatchLogsTest : CloudWatchLogsContract(FakeCloudWatchLogs()) {
    override val aws = fakeAwsEnvironment
}
