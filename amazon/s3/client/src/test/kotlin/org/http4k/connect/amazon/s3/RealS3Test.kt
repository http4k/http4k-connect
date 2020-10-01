package org.http4k.connect.amazon.s3

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.cloudnative.env.fromConfigFile
import org.http4k.core.HttpHandler
import org.http4k.lens.composite
import org.junit.jupiter.api.Test
import java.io.File
import java.util.UUID

interface S3Contract {
    val http: HttpHandler
    val credentials: AwsCredentials
    val scope: AwsCredentialScope

    @Test
    fun `lifecycle`() {
        val bucketName = BucketName(UUID.randomUUID().toString())
        val http1 = S3.Http(http, scope, { credentials })
    }
}

class RealS3Test : S3Contract {
    override val credentials: AwsCredentials
    override val scope: AwsCredentialScope

    init {
        val env = Environment.fromConfigFile(File(System.getProperty("user.home"), ".aws/config")) overrides
            Environment.fromConfigFile(File(System.getProperty("user.home"), ".aws/credentials"))
        credentials = EnvironmentKey.composite {
            AwsCredentials(
                EnvironmentKey.required("default-aws-secret-access-key")(it),
                EnvironmentKey.required("default-aws-access-key-id")(it)
            )
        }(env)
        scope = AwsCredentialScope(EnvironmentKey.required("default-region")(env), "s3")
    }

    override val http = JavaHttpClient()
}
