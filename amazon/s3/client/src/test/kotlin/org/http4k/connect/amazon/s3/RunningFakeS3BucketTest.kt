package org.http4k.connect.amazon.s3

import org.http4k.client.JavaHttpClient
import org.http4k.connect.defaultPort
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach

class RunningFakeS3BucketTest : S3BucketContract(
    ClientFilters.SetAuthorityFrom(Uri.of("http://localhost:" + FakeS3::class.defaultPort()))
        .then(JavaHttpClient())
) {
    override val aws = fakeAwsEnvironment
    private lateinit var server: Http4kServer

    override fun setup() {
        server = FakeS3().start()
    }

    @AfterEach
    fun stop() {
        server.stop()
    }
}
