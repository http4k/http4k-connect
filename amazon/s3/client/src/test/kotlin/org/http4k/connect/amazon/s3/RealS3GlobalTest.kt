package org.http4k.connect.amazon.s3

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.RealAwsEnvironment
import org.http4k.connect.amazon.configAwsEnvironment

class RealS3GlobalTest : S3GlobalContract(JavaHttpClient()), RealAwsEnvironment {
    override val aws = configAwsEnvironment(service)
}
