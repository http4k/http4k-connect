package org.http4k.connect.amazon.s3

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.Listing
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.BucketName
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class S3BucketContract(http: HttpHandler): AwsContract(AwsService.of("s3"), http) {

    private val bucket = BucketName.of(UUID.randomUUID().toString())

    private val s3Bucket by lazy {
        S3.Bucket.Http(bucket, aws.scope, { aws.credentials }, http)
    }

    private val key = BucketKey.of(UUID.randomUUID().toString())

    @BeforeEach
    fun deleteBucket() {
        s3Bucket.delete(key).successValue()
        s3Bucket.delete().successValue()
    }

    @Test
    fun `bucket key lifecycle`() {
        with(s3Bucket) {
            val newKey = BucketKey.of(UUID.randomUUID().toString())

            assertThat(create().successValue(), equalTo(Unit))

            assertThat(list().successValue(), equalTo(Listing.Empty))
            assertThat(get(key).successValue(), absent())
            assertThat(set(key, "hello".byteInputStream()).successValue(), equalTo(Unit))
            assertThat(String(get(key).successValue()!!.readBytes()), equalTo("hello"))
            assertThat(list().successValue(), equalTo(Listing.Unpaged(listOf(key))))
            assertThat(set(key, "there".byteInputStream()).successValue(), equalTo(Unit))
            assertThat(String(get(key).successValue()!!.readBytes()), equalTo("there"))

            assertThat(copy(key, newKey).successValue(), equalTo(Unit))
            assertThat(String(get(newKey).successValue()!!.readBytes()), equalTo("there"))
            assertThat(list().successValue(), equalTo(Listing.Unpaged(listOf(key, newKey).sortedBy { it.value })))
            assertThat(delete(newKey).successValue(), equalTo(Unit))
            assertThat(delete(key).successValue(), equalTo(Unit))
            assertThat(get(key).successValue(), equalTo(null))
            assertThat(list().successValue(), equalTo(Listing.Empty))
            assertThat(delete().successValue(), equalTo(Unit))
        }
    }
}
