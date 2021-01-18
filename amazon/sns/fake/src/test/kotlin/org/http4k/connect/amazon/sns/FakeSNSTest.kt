package org.http4k.connect.amazon.sns

import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.filter.debug

class FakeSNSTest : SNSContract(FakeSNS().debug()) {
    override val aws = fakeAwsEnvironment
}
