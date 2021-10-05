package org.http4k.connect.amazon.ses

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeSESTest : SESContract(FakeSES()) {
    override val aws = fakeAwsEnvironment
}
