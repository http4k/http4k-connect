package org.http4k.connect.amazon.s3

import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.configAwsEnvironment

class RealS3BucketTest : S3BucketContract(JavaHttpClient()) {
   override val aws get() = configAwsEnvironment(service)
}
