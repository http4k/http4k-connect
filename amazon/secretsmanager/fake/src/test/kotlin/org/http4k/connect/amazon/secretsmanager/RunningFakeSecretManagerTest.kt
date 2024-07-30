package org.http4k.connect.amazon.secretsmanager

import org.http4k.chaos.defaultLocalUri
import org.http4k.chaos.start
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach

class RunningFakeSecretManagerTest : SecretsManagerContract(
    SetHostFrom(FakeSecretsManager::class.defaultLocalUri).then(JavaHttpClient())
) {
    override val nameOrArn = ARN.of("arn:aws:secretsmanager:us-west-2:123456789012:secret:MYSECRET").value

    override val aws = fakeAwsEnvironment
    private lateinit var server: Http4kServer

    override fun setUp() {
        server = FakeSecretsManager().start()
    }

    @AfterEach
    fun stop() {
        server.stop()
    }
}
