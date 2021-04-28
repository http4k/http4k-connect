package org.http4k.connect.amazon.cognito

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.defaultLocalUri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Disabled

@Disabled
class RunningFakeCognitoTest : CognitoContract(
    SetHostFrom(FakeCognito::class.defaultLocalUri).then(JavaHttpClient())
) {
    override val aws = fakeAwsEnvironment
    private lateinit var server: Http4kServer

    override fun setUp() {
        server = FakeCognito().start()
    }

    @AfterEach
    fun stop() {
        server.stop()
    }
}
