package org.http4k.connect.storage

import HttpStorageClient
import HttpStorageServer
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
class HttpStorageClientTest : StorageContract() {
    override val storage: Storage<AnEntity> = HttpStorageClient(HttpStorageServer<AnEntity>(Storage.InMemory()))
}



