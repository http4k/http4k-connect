package org.http4k.connect.amazon.systemsmanager

import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.amazon.secretsmanager.SystemsManagerContract

class FakeSystemsManagerTest : SystemsManagerContract(FakeSystemsManager()) {
    override val aws = fakeAwsEnvironment
}
