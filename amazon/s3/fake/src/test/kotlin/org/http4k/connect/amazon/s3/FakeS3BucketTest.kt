package org.http4k.connect.amazon.s3

import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.filter.debug

class FakeS3BucketTest : S3BucketContract(FakeS3().debug()) {
    override val aws = fakeAwsEnvironment
}
