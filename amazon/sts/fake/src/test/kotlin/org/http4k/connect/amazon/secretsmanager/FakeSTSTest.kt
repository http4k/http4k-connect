package org.http4k.connect.amazon.secretsmanager

import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.amazon.sts.FakeSTS
import org.http4k.connect.amazon.sts.STSContract
import java.time.Clock

class FakeSTSTest : STSContract(FakeSTS(Clock.systemDefaultZone())) {
    override val aws = fakeAwsEnvironment
}
