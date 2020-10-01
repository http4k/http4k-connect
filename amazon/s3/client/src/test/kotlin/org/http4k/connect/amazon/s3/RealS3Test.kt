package org.http4k.connect.amazon.s3

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.cloudnative.env.fromConfigFile
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.lens.composite
import org.junit.jupiter.api.Test
import java.io.File

interface S3Contract {
    val http: HttpHandler
    val credentials: AwsCredentials
    val scope: AwsCredentialScope

    @Test
    fun `lifecycle`() {
        val http1 = S3.Http(DebuggingFilters.PrintRequestAndResponse().then(http), scope, { credentials })

        println(http1.buckets())

    }
}

class RealS3Test : S3Contract {
    override val credentials: AwsCredentials
    override val scope: AwsCredentialScope
    override val http: HttpHandler

    init {
        val env = Environment.fromConfigFile(File(System.getProperty("user.home"), ".aws/config")) overrides
            Environment.fromConfigFile(File(System.getProperty("user.home"), ".aws/credentials"))
        credentials = EnvironmentKey.composite {
            AwsCredentials(
                EnvironmentKey.required("default-aws-secret-access-key")(it),
                EnvironmentKey.required("default-aws-access-key-id")(it)
            )
        }(env)
        val region = EnvironmentKey.required("default-region")(env)
        scope = AwsCredentialScope(region, "s3")
        http = JavaHttpClient()
        println(http(Request(Method.GET, Uri.of("https://s3.$region.amazonaws.com/"))))
    }
}

