package org.http4k.connect.amazon

import org.http4k.connect.amazon.model.AwsService
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeEach

interface RealAwsEnvironment {
    val service: AwsService
    val aws: AwsEnvironment

    @BeforeEach
    fun loadConfig() {
        try {
            aws
        } catch (e: Exception) {
            Assumptions.assumeTrue(false)
        }
    }
}
