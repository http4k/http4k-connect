package org.http4k.connect.amazon.s3

import org.http4k.client.JavaHttpClient
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled

@Disabled("not using this yet")
class RealS3BucketTest : S3BucketContract(JavaHttpClient()) {
    override val aws get() = configAwsEnvironment()

    @BeforeEach
    fun loadConfig() {
        try {
            aws
        } catch (e: Exception) {
            assumeTrue(false)
        }
    }
}
