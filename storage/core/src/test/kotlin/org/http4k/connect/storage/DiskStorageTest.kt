package org.http4k.connect.storage

import org.testcontainers.shaded.com.google.common.io.Files

class DiskStorageTest : StorageContract() {
    override val storage = Storage.Disk<AnEntity>(Files.createTempDir().apply { deleteOnExit() })
}
