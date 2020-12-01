package org.http4k.connect.amazon.secretsmanager

import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.amazon.sts.FakeSTS
import org.http4k.connect.amazon.sts.STSContract

class FakeSTSTest : STSContract(FakeSTS()) {
    override val aws = fakeAwsEnvironment
}
