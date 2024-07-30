package org.http4k.connect.amazon

interface FakeAwsContract : AwsContract {
    override val aws: AwsEnvironment get() = fakeAwsEnvironment
}

