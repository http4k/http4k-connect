package org.http4k.connect.amazon.cloudfront

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment

class RealCloudFrontTest : CloudFrontContract(), RealAwsEnvironment {
    override val http = JavaHttpClient()
}
