package org.http4k.connect.amazon.s3

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.defaultPort
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach

class RunningFakeS3BucketTest : S3BucketContract(
    SetBaseUriFrom(Uri.of("http://localhost:" + FakeS3::class.defaultPort()))
        .then(JavaHttpClient())
) {
    override val aws = fakeAwsEnvironment
    private lateinit var server: Http4kServer

    override fun setUp() {
        server = FakeS3().start()
    }

    @AfterEach
    fun stop() {
        server.stop()
    }
}
