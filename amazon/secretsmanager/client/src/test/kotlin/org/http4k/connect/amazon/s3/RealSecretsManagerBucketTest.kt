package org.http4k.connect.amazon.s3

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.configAwsEnvironment
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach

class RealSecretsManagerBucketTest : SecretsManagerBucketContract(JavaHttpClient()) {
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
