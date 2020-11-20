package org.http4k.connect.amazon.secretsmanager

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.configAwsEnvironment
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach

class RealSecretsManagerBucketTest : SecretsManagerContract(JavaHttpClient()) {
    override val aws get() = configAwsEnvironment("secretsmanager")

    @BeforeEach
    fun loadConfig() {
        try {
            aws
        } catch (e: Exception) {
            assumeTrue(false)
        }
    }
}
