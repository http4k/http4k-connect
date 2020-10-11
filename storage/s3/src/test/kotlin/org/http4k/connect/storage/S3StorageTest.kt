package org.http4k.connect.storage

import org.http4k.connect.amazon.s3.BucketName
import org.http4k.connect.amazon.s3.FakeS3

class S3StorageTest : StorageContract() {
    override val storage: Storage<AnEntity> by lazy {
        Storage.S3(FakeS3().s3BucketClient(BucketName("foobar")))
    }
}
