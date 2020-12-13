package org.http4k.connect.amazon.secretsmanager

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.defaultPort
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach

class RunningFakeSecretManagerTest : SecretsManagerContract(
    SetBaseUriFrom(Uri.of("http://localhost:" + FakeSecretsManager::class.defaultPort()))
        .then(JavaHttpClient())
) {
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
