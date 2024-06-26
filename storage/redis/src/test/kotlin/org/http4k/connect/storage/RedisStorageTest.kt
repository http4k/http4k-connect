package org.http4k.connect.storage

import org.http4k.connect.assumeDockerDaemonRunning
import org.http4k.core.Uri
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
    val redis = GenericContainer(DockerImageName.parse("redis:7.2.5-alpine")).withExposedPorts(6379)

    override val storage: Storage<AnEntity> by lazy {
        Storage.Redis<AnEntity>(Uri.of("redis://${redis.host}:${redis.firstMappedPort}"))
    }
}
