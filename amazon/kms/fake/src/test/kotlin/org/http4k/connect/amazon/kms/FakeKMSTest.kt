package org.http4k.connect.amazon.kms

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeKMSTest : KMSContract(FakeKMS()) {
    override val aws = fakeAwsEnvironment
}
