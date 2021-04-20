package org.http4k.connect.amazon.s3

import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.filter.debug
import java.util.UUID

class FakeS3BucketTest : S3BucketContract(FakeS3()) {
    override val aws = fakeAwsEnvironment
    override val bucket = BucketName.of(UUID.randomUUID().toString())
}

class FakeS3BucketPathStyleTest : S3BucketContract(FakeS3().debug()) {
    override val aws = fakeAwsEnvironment
    override val bucket = BucketName.of(UUID.randomUUID().toString().replace('-', '.'))
}

class FakeS3GlobalTest : S3GlobalContract(FakeS3()) {
    override val aws = fakeAwsEnvironment
}
