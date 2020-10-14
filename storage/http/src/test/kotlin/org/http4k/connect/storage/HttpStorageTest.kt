package org.http4k.connect.storage

class HttpStorageTest : StorageContract() {
    override val storage: Storage<AnEntity> =
        Storage.Http(
            StorageExplorer<AnEntity>(Storage.InMemory())
        )
}


