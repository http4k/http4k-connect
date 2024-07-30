package org.http4k.connect.amazon.containercredentials

import org.http4k.chaos.defaultLocalUri
import org.http4k.chaos.start
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.FakeAwsContract
import org.http4k.connect.amazon.containerCredentials.ContainerCredentialsContract
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

class RunningFakeContainerCredentialsTest : ContainerCredentialsContract, FakeAwsContract {
    override val http = SetHostFrom(FakeContainerCredentials::class.defaultLocalUri).then(JavaHttpClient())
    override val fullUri = Uri.of("http://localhost:80/foobar")

    private lateinit var server: Http4kServer

    @BeforeEach
    fun setUp() {
        server = FakeContainerCredentials(clock).start()
    }

    @AfterEach
    fun stop() {
        server.stop()
    }
}
