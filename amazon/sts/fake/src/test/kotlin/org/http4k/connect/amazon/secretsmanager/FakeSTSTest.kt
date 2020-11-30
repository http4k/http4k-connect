package org.http4k.connect.amazon.secretsmanager

import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.amazon.sts.FakeSTS
import org.http4k.connect.amazon.sts.StsContract

class FakeSTSTest : StsContract(FakeSTS()) {
    override val aws = fakeAwsEnvironment
}
