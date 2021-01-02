package org.http4k.connect.amazon.sts

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeSTSTest : STSContract(FakeSTS()) {
    override val aws = fakeAwsEnvironment
}
