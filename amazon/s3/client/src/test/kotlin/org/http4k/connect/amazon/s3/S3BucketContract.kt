package org.http4k.connect.amazon.s3

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.s3.action.ObjectList
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


abstract class S3BucketContract(http: HttpHandler) : AwsContract() {

    abstract val bucket: BucketName

    protected val s3Bucket by lazy {
        S3Bucket.Http(bucket, aws.region, { aws.credentials }, http)
    }

    private val s3 by lazy {
        S3.Http({ aws.credentials }, http)
    }

    protected val key = BucketKey.of("originalKey")

    @BeforeEach
    fun recreate() {
        s3Bucket.deleteObject(key)
        s3Bucket.deleteBucket()
        s3.createBucket(bucket, aws.region).successValue()
    }

    @Test
    fun `bucket key lifecycle`() {
        waitForBucketCreation()
        try {
            assertThat(s3Bucket.headBucket().successValue(), equalTo(Unit))

            val newKey = BucketKey.of("newKey")

            assertThat(s3Bucket.listObjectsV2().successValue(), equalTo(ObjectList(emptyList())))
            assertThat(s3Bucket[key].successValue(), absent())
            assertThat(s3Bucket.set(key, "hello".byteInputStream()).successValue(), equalTo(Unit))
            assertThat(String(s3Bucket[key].successValue()!!.readBytes()), equalTo("hello"))
            assertThat(s3Bucket.listObjectsV2().successValue().items.map { it.Key }, equalTo(listOf(key)))
            assertThat(s3Bucket.set(key, "there".byteInputStream()).successValue(), equalTo(Unit))
            assertThat(String(s3Bucket[key].successValue()!!.readBytes()), equalTo("there"))

            assertThat(s3Bucket.copyObject(bucket, key, newKey).successValue(), equalTo(Unit))
            assertThat(String(s3Bucket[newKey].successValue()!!.readBytes()), equalTo("there"))
            assertThat(
                s3Bucket.listObjectsV2().successValue().items.map { it.Key },
                equalTo(listOf(key, newKey).sortedBy { it.value })
            )
            assertThat(s3Bucket.deleteObject(newKey).successValue(), equalTo(Unit))
            assertThat(s3Bucket.deleteObject(key).successValue(), equalTo(Unit))
            assertThat(s3Bucket[key].successValue(), equalTo(null))
            assertThat(s3Bucket.listObjectsV2().successValue(), equalTo(ObjectList(emptyList())))
        } finally {
            s3Bucket.deleteObject(key)
            s3Bucket.deleteBucket()
        }
    }

    open fun waitForBucketCreation() {}
}
