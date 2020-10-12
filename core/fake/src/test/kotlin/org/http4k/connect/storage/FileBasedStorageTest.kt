package org.http4k.connect.storage

import org.testcontainers.shaded.com.google.common.io.Files

class FileBasedStorageTest : StorageContract() {
    override val storage = Storage.FileBased<AnEntity>(Files.createTempDir().apply { deleteOnExit() })
}
