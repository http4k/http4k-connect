package org.http4k.connect.amazon.kms

import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.amazon.systemsmanager.FakeKMS

class FakeKMSTest : KMSContract(FakeKMS()) {
    override val aws = fakeAwsEnvironment
}
