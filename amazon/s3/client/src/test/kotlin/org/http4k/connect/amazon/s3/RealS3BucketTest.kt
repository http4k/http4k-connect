package org.http4k.connect.amazon.s3

import debug
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.configAwsEnvironment
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach

class RealS3BucketTest : S3BucketContract(JavaHttpClient().debug()) {
    override val aws get() = configAwsEnvironment("s3")

    @BeforeEach
    fun loadConfig() {
        try {
            aws
        } catch (e: Exception) {
            assumeTrue(false)
        }
    }
}
