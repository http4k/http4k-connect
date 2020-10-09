package org.http4k.connect.storage

import org.http4k.connect.assumeDockerDaemonRunning
import org.http4k.core.Uri
import org.http4k.format.RedisJackson
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
class RedisStorageTest : StorageContract() {

    init {
        assumeDockerDaemonRunning()
    }

    @Container
    val redis = GenericContainer<GenericContainer<*>>(DockerImageName.parse("redis:5.0.3-alpine"))
        .withExposedPorts(6379)

    override val storage: Storage<String> by lazy {
        Storage.RedisJackson<String>(Uri.of("redis://${redis.host}:${redis.firstMappedPort}"))
    }
}
