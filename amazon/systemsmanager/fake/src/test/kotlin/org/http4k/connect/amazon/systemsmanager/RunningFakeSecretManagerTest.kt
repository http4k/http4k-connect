package org.http4k.connect.amazon.systemsmanager

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.amazon.secretsmanager.SystemsManagerContract
import org.http4k.connect.defaultPort
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach

class RunningFakeSecretManagerTest : SystemsManagerContract(
    SetBaseUriFrom(Uri.of("http://localhost:" + FakeSystemsManager::class.defaultPort()))
        .then(JavaHttpClient())
) {
    override val aws = fakeAwsEnvironment
    private lateinit var server: Http4kServer

    override fun setUp() {
        server = FakeSystemsManager().start()
    }

    @AfterEach
    fun stop() {
        server.stop()
    }
}
