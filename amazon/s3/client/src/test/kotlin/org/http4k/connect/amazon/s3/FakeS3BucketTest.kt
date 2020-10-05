package org.http4k.connect.amazon.s3

class FakeS3BucketTest : S3BucketContract(FakeS3()) {
    override val aws = fakeAwsEnvironment
}
