package org.http4k.connect.storage

import org.http4k.core.Uri
import org.http4k.format.Jackson
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@Disabled
class RedisStorageTest : StorageContract() {

    @Container
    val redis = GenericContainer<GenericContainer<*>>("redis:5.0.3-alpine").withExposedPorts(6379)

    @BeforeEach
    override fun setUp() {
        println("starting")
        redis.start()
    }

    override val storage: Storage<String> by lazy {
        Storage.Redis<String>(Uri.of("redis://${redis.host}}:${redis.firstMappedPort}"), Jackson)
    }
}
