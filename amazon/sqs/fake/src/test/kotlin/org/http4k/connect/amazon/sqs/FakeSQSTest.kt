package org.http4k.connect.amazon.sqs

import org.http4k.connect.amazon.fakeAwsEnvironment
import java.time.Clock

class FakeSQSTest : SQSContract(FakeSQS(Clock.systemDefaultZone())) {
    override val aws = fakeAwsEnvironment
}
