package org.http4k.connect.amazon.sts

import org.http4k.connect.amazon.fakeAwsEnvironment
import java.time.Clock

class FakeSTSTest : STSContract(FakeSTS(Clock.systemDefaultZone())) {
    override val aws = fakeAwsEnvironment
}
