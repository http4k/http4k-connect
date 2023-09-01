package org.http4k.connect.amazon.evidently

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeEvidentlyContract: EvidentlyContract(FakeEvidently()) {
    override val aws = fakeAwsEnvironment
}
