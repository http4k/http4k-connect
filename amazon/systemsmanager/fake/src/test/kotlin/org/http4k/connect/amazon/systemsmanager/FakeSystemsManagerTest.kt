package org.http4k.connect.amazon.systemsmanager

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeSystemsManagerTest : SystemsManagerContract(FakeSystemsManager()) {
    override val aws = fakeAwsEnvironment
}
