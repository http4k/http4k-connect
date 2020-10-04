package org.http4k.connect.amazon.s3

class FakeS3BucketTest : S3BucketContract(FakeS3Bucket()) {
    override val aws = fakeAwsEnvironment
}
