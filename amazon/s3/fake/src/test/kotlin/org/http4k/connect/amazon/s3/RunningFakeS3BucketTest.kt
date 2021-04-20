package org.http4k.connect.amazon.s3

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.connect.defaultLocalUri
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetHostFrom
import org.http4k.server.Http4kServer
import org.junit.jupiter.api.AfterEach
import java.util.UUID

class RunningFakeS3BucketTest : S3BucketContract(
    SetHostFrom(FakeS3::class.defaultLocalUri).then(JavaHttpClient())
) {
    override val aws = fakeAwsEnvironment
    private lateinit var server: Http4kServer

    override val bucket = BucketName.of(UUID.randomUUID().toString())

    override fun setUp() {
        server = FakeS3().start()
    }

    @AfterEach
    fun stop() {
        server.stop()
    }
}
