package org.http4k.connect.amazon.s3

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.cloudnative.env.fromConfigFile
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.lens.composite
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.util.UUID

abstract class S3GlobalOperationsContract {
    abstract val http: HttpHandler
    abstract val uri: Uri
    abstract val credentials: AwsCredentials
    abstract val scope: AwsCredentialScope

    private val s3 by lazy {
        S3.Http(uri, http, scope, { credentials })
    }

    private val bucket = BucketName(UUID.randomUUID().toString())

    @BeforeEach
    fun cleanup() {
        s3.delete(bucket).successValue()
    }

    @Test
    fun `bucket lifecycle`() {
        assertThat(s3.buckets().successValue().contains(bucket), equalTo(false))
        assertThat(s3.create(bucket), equalTo(Success(Unit)))
        assertThat(s3.buckets().successValue().contains(bucket), equalTo(true))
        assertThat(s3.create(bucket), equalTo(Success(Unit)))
        assertThat(s3.delete(bucket), equalTo(Success(Unit)))
        assertThat(s3.buckets().successValue().contains(bucket), equalTo(false))
    }
}

private fun <T, E> Result<T, E>.successValue(): T = when (this) {
    is Success -> value
    is Failure -> throw AssertionError("Failed: $reason")
}

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

class FakeS3GlobalOperationsTest : S3GlobalOperationsContract() {
    override val credentials = AwsCredentials("key", "keyid")
    override val scope = AwsCredentialScope("ldn-north-1", "s3")
    override val http = JavaHttpClient()
    override val uri = Uri.of("http://localhost:4569")
}
