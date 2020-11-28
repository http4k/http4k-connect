package org.http4k.connect.amazon.s3

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsTest
import org.http4k.connect.amazon.configAwsEnvironment

class RealS3GlobalTest : S3GlobalContract(JavaHttpClient()), RealAwsTest {
    override val aws = configAwsEnvironment(service)
}
