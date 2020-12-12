package org.http4k.connect.storage

import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.s3.Create
import org.http4k.connect.amazon.s3.FakeS3

class S3StorageTest : StorageContract() {
    override val storage: Storage<AnEntity> by lazy {
        Storage.S3(FakeS3().s3BucketClient(BucketName.of("foobar"), Region.of("ldn-north-1")).apply {
            this(Create())
        })
    }
}
