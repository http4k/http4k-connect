package org.http4k.connect.amazon.sqs

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeSQSTest : SQSContract(FakeSQS()) {
    override val aws = fakeAwsEnvironment
}
