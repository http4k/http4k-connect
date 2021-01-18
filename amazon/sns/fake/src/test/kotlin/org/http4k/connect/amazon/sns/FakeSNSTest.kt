package org.http4k.connect.amazon.sns

import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.amazon.sqs.FakeSNS
import org.http4k.connect.amazon.sqs.SNSContract

class FakeSNSTest : SNSContract(FakeSNS()) {
    override val aws = fakeAwsEnvironment
}
