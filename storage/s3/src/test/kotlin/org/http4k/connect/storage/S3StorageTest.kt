package org.http4k.connect.storage

import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.s3.FakeS3
import org.http4k.connect.amazon.s3.action.CreateBucket

class S3StorageTest : StorageContract() {
    override val storage: Storage<AnEntity> by lazy {
        val bucketName = BucketName.of("foobar")
        val region = Region.of("ldn-north-1")
        val fakeS3 = FakeS3().apply {
            this.s3Client()(CreateBucket(bucketName, region))
        }
        Storage.S3(fakeS3.s3BucketClient(bucketName, region))
    }
}
