package org.http4k.connect.amazon.secretsmanager

import org.http4k.chaos.defaultLocalUri
import org.http4k.chaos.start
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.FakeAwsContract
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

class RunningFakeSecretManagerTest : SecretsManagerContract, FakeAwsContract {
    override val http = SetHostFrom(FakeSecretsManager::class.defaultLocalUri).then(JavaHttpClient())
    override val nameOrArn = ARN.of("arn:aws:secretsmanager:us-west-2:123456789012:secret:MYSECRET").value

    override val aws = fakeAwsEnvironment
    private lateinit var server: Http4kServer

    @BeforeEach
    fun setUp() {
        server = FakeSecretsManager().start()
    }

    @AfterEach
    fun stop() {
        server.stop()
    }
}
