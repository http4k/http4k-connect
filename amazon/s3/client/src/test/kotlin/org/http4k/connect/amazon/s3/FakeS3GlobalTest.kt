package org.http4k.connect.amazon.s3

class FakeS3GlobalTest : S3GlobalContract(FakeS3()) {
    override val aws = fakeAwsEnvironment
}
