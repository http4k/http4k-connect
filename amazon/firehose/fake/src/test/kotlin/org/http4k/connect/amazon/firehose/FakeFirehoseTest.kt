package org.http4k.connect.amazon.firehose

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeFirehoseTest : FirehoseContract(FakeFirehose()) {
    override val aws = fakeAwsEnvironment
}
