package org.http4k.connect.amazon.s3

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class S3GlobalContract(http: HttpHandler) : AwsContract(AwsService.of("s3"), http) {
    private val s3 by lazy {
        S3.Http(aws.scope, { aws.credentials }, http)
    }

    private val bucket = BucketName.of(UUID.randomUUID().toString())

    @BeforeEach
    fun deleteBucket() {
        s3(DeleteBucket(bucket)).successValue()
    }

    @Test
    fun `bucket lifecycle`() {
        assertThat(s3(ListBuckets()).successValue().contains(bucket), equalTo(false))
        assertThat(s3(CreateBucket(bucket, Region.of(aws.scope.region))), equalTo(Success(Unit)))
        assertThat(s3(ListBuckets()).successValue().contains(bucket), equalTo(true))
        assertThat(s3(DeleteBucket(bucket)), equalTo(Success(Unit)))
        assertThat(s3(ListBuckets()).successValue().contains(bucket), equalTo(false))
    }
}

