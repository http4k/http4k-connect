package org.http4k.connect.amazon.secretsmanager

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeSecretsManagerTest : SecretsManagerContract(FakeSecretsManager()) {
    override val aws = fakeAwsEnvironment
}
