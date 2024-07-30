package org.http4k.connect.amazon.cloudfront

import org.http4k.connect.amazon.fakeAwsEnvironment

class FakeCloudFrontTest : CloudFrontContract(FakeCloudFront()) {
    override val aws = fakeAwsEnvironment
}
