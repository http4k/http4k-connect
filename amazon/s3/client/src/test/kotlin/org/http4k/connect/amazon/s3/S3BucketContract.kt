package org.http4k.connect.amazon.s3

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.Listing
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class S3BucketContract(http: HttpHandler) : AwsContract(http) {

    private val bucket = BucketName.of(UUID.randomUUID().toString())

    private val s3Bucket by lazy {
        S3Bucket.Http(bucket, aws.region, { aws.credentials }, http)
    }

    private val s3 by lazy {
        S3.Http(aws.region, { aws.credentials }, http)
    }

    private val key = BucketKey.of(UUID.randomUUID().toString())

    @BeforeEach
    fun recreate() {
        s3Bucket.deleteKey(key).successValue()
        s3.deleteBucket(bucket)
        s3.createBucket(bucket, aws.region).successValue()
    }

    @Test
    fun `bucket key lifecycle`() {
        Thread.sleep(10000)
        try {
            val newKey = BucketKey.of(UUID.randomUUID().toString())

            assertThat(s3Bucket.listKeys().successValue(), equalTo(Listing.Empty))
            assertThat(s3Bucket[key].successValue(), absent())
            assertThat(s3Bucket.set(key, "hello".byteInputStream()).successValue(), equalTo(Unit))
            assertThat(String(s3Bucket[key].successValue()!!.readBytes()), equalTo("hello"))
            assertThat(s3Bucket.listKeys().successValue(), equalTo(Listing.Unpaged(listOf(key))))
            assertThat(s3Bucket.set(key, "there".byteInputStream()).successValue(), equalTo(Unit))
            assertThat(String(s3Bucket[key].successValue()!!.readBytes()), equalTo("there"))

            assertThat(s3Bucket.copyKey(bucket, key, newKey).successValue(), equalTo(Unit))
            assertThat(String(s3Bucket[newKey].successValue()!!.readBytes()), equalTo("there"))
            assertThat(s3Bucket.listKeys().successValue(), equalTo(Listing.Unpaged(listOf(key, newKey).sortedBy { it.value })))
            assertThat(s3Bucket.deleteKey(newKey).successValue(), equalTo(Unit))
            assertThat(s3Bucket.deleteKey(key).successValue(), equalTo(Unit))
            assertThat(s3Bucket[key].successValue(), equalTo(null))
            assertThat(s3Bucket.listKeys().successValue(), equalTo(Listing.Empty))
        } finally {
            s3Bucket.deleteKey(key).successValue()
            s3.deleteBucket(bucket)
        }
    }
}
