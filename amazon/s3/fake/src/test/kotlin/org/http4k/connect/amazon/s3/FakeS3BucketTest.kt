package org.http4k.connect.amazon.s3

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeS3BucketTest : S3BucketContract(FakeS3()) {
    override val aws = fakeAwsEnvironment
}
