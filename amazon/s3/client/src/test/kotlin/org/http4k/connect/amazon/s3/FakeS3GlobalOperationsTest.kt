package org.http4k.connect.amazon.s3

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.core.Uri

class FakeS3GlobalOperationsTest : S3GlobalOperationsContract() {
    override val credentials = AwsCredentials("key", "keyid")
    override val scope = AwsCredentialScope("ldn-north-1", "s3")
    override val http = FakeS3()
    override val uri = Uri.of("http://localhost:4569")
}
