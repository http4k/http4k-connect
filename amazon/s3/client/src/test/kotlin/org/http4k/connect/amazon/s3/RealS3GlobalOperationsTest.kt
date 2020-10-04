package org.http4k.connect.amazon.s3

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.cloudnative.env.fromConfigFile
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.lens.composite
import java.io.File

class RealS3GlobalOperationsTest : S3GlobalOperationsContract() {
    override val credentials: AwsCredentials
    override val scope: AwsCredentialScope
    override val http: HttpHandler
    override val uri: Uri

    init {
        val env = Environment.fromConfigFile(File(System.getProperty("user.home"), ".aws/config")) overrides
            Environment.fromConfigFile(File(System.getProperty("user.home"), ".aws/credentials"))
        credentials = EnvironmentKey.composite {
            AwsCredentials(
                EnvironmentKey.required("default-aws-access-key-id")(it),
                EnvironmentKey.required("default-aws-secret-access-key")(it)
            )
        }(env)

        val region = EnvironmentKey.required("default-region")(env)
        scope = AwsCredentialScope(region, "s3")
        uri = Uri.of("https://s3.amazonaws.com/")
        http = JavaHttpClient()
    }
}
