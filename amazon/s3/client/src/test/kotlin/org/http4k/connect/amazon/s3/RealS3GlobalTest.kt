package org.http4k.connect.amazon.s3

import org.http4k.client.JavaHttpClient
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach

class RealS3GlobalTest : S3GlobalContract(JavaHttpClient()) {
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
