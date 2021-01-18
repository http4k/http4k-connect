package org.http4k.connect.amazon.sns

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeSNSTest : SNSContract(FakeSNS()) {
    override val aws = fakeAwsEnvironment
}
