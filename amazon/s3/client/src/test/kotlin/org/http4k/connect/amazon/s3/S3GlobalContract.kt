package org.http4k.connect.amazon.s3

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class S3GlobalContract(private val http: HttpHandler) {
    abstract val aws: AwsEnvironment

    private val s3 by lazy {
        S3.Http(aws.uri, PrintRequestAndResponse().then(http), aws.scope, { aws.credentials })
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

