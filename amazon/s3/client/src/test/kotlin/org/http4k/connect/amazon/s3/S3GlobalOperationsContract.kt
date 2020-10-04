package org.http4k.connect.amazon.s3

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class S3GlobalOperationsContract {
    abstract val http: HttpHandler
    abstract val uri: Uri
    abstract val credentials: AwsCredentials
    abstract val scope: AwsCredentialScope

    private val s3 by lazy {
        S3.Http(uri, PrintRequestAndResponse().then(http), scope, { credentials })
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

