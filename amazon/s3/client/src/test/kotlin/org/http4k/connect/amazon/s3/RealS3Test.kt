package org.http4k.connect.amazon.s3

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.map
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.cloudnative.env.fromConfigFile
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.lens.composite
import org.junit.jupiter.api.Test
import java.io.File

interface S3Contract {
    val http: HttpHandler
    val uri: Uri
    val credentials: AwsCredentials
    val scope: AwsCredentialScope

    @Test
    fun lifecycle() {
        val http = S3.Http(uri, DebuggingFilters.PrintRequestAndResponse().then(http), scope, { credentials })
        http.buckets().map { assertThat(it.count(), equalTo(0)) }
    }
}

class RealS3Test : S3Contract {
    override val credentials: AwsCredentials
    override val scope: AwsCredentialScope
    override val http: HttpHandler
    override val uri: Uri

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
        uri = Uri.of("https://s3.$region.amazonaws.com/")
        http = JavaHttpClient()
    }
}

class FakeS3Test : S3Contract {
    override val credentials = AwsCredentials("key", "keyid")
    override val scope = AwsCredentialScope("ldn-north-1", "s3")
    override val http = JavaHttpClient()
    override val uri = Uri.of("http://localhost:4569")
}
