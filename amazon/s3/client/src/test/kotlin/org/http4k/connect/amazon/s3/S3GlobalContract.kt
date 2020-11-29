package org.http4k.connect.amazon.s3

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class S3GlobalContract(http: HttpHandler): AwsContract(AwsService.of("s3"), http) {
    private val s3 by lazy {
        S3.Http(aws.scope, { aws.credentials }, http)
    }

    private val bucket = BucketName.of(UUID.randomUUID().toString())

    @BeforeEach
    fun deleteBucket() {
        s3.delete(bucket).successValue()
    }

    @Test
    fun `bucket lifecycle`() {
        with(s3) {
            assertThat(buckets().successValue().contains(bucket), equalTo(false))
            assertThat(create(bucket), equalTo(Success(Unit)))
            assertThat(buckets().successValue().contains(bucket), equalTo(true))
            assertThat(delete(bucket), equalTo(Success(Unit)))
            assertThat(buckets().successValue().contains(bucket), equalTo(false))
        }
    }
}

