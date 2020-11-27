package org.http4k.connect.amazon.kms

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.configAwsEnvironment
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach

class RealKMSTest : KMSContract(JavaHttpClient()) {
    override val aws get() = configAwsEnvironment("kms")

    @BeforeEach
    fun loadConfig() {
        try {
            aws
        } catch (e: Exception) {
            assumeTrue(false)
        }
    }
}
