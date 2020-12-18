package org.http4k.connect.amazon.s3

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.Listing
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.s3.action.CopyKey
import org.http4k.connect.amazon.s3.action.CreateBucket
import org.http4k.connect.amazon.s3.action.DeleteBucket
import org.http4k.connect.amazon.s3.action.DeleteKey
import org.http4k.connect.amazon.s3.action.ListKeys
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class S3BucketContract(http: HttpHandler) : AwsContract(AwsService.of("s3"), http) {

    private val bucket = BucketName.of(UUID.randomUUID().toString())

    private val s3Bucket by lazy {
        S3.Bucket.Http(bucket, aws.scope, { aws.credentials }, http)
    }

    private val s3 by lazy {
        S3.Http(aws.scope, { aws.credentials }, http)
    }

    private val key = BucketKey.of(UUID.randomUUID().toString())

    @BeforeEach
    fun recreate() {
        s3(DeleteBucket(bucket))
        s3(CreateBucket(bucket, Region.of(aws.scope.region)))
    }

    @AfterEach
    fun deleteBucket() {
        s3Bucket(DeleteKey(key)).successValue()
        s3(DeleteBucket(bucket))
    }

    @Test
    fun `bucket key lifecycle`() {
        val newKey = BucketKey.of(UUID.randomUUID().toString())

        assertThat(s3Bucket(ListKeys()).successValue(), equalTo(Listing.Empty))
        assertThat(s3Bucket[key].successValue(), absent())
        assertThat(s3Bucket.set(key, "hello".byteInputStream()).successValue(), equalTo(Unit))
        assertThat(String(s3Bucket[key].successValue()!!.readBytes()), equalTo("hello"))
        assertThat(s3Bucket(ListKeys()).successValue(), equalTo(Listing.Unpaged(listOf(key))))
        assertThat(s3Bucket.set(key, "there".byteInputStream()).successValue(), equalTo(Unit))
        assertThat(String(s3Bucket[key].successValue()!!.readBytes()), equalTo("there"))

        assertThat(s3Bucket(CopyKey(bucket, key, newKey)).successValue(), equalTo(Unit))
        assertThat(String(s3Bucket[newKey].successValue()!!.readBytes()), equalTo("there"))
        assertThat(s3Bucket(ListKeys()).successValue(), equalTo(Listing.Unpaged(listOf(key, newKey).sortedBy { it.value })))
        assertThat(s3Bucket(DeleteKey(newKey)).successValue(), equalTo(Unit))
        assertThat(s3Bucket(DeleteKey(key)).successValue(), equalTo(Unit))
        assertThat(s3Bucket[key].successValue(), equalTo(null))
        assertThat(s3Bucket(ListKeys()).successValue(), equalTo(Listing.Empty))
    }
}
