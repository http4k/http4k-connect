package org.http4k.connect.amazon

import org.junit.jupiter.api.BeforeEach

abstract class AwsContract {
    abstract val aws: AwsEnvironment

    open fun setUp() {}

    @BeforeEach
    fun cleanup() {
        setUp()
    }
}
