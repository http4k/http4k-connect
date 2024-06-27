package org.http4k.connect.amazon.s3

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.containsSubstring
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.or
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.core.model.Tag
import org.http4k.connect.amazon.s3.action.ObjectList
import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.connect.amazon.s3.model.S3BucketPreSigner
import org.http4k.connect.errorValue
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.PUT
import org.http4k.core.Request
import org.http4k.core.Status.Companion.CREATED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.NO_CONTENT
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.ClientFilters.SetXForwardedHost
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Duration


abstract class S3BucketContract(protected val http: HttpHandler) : AwsContract() {

    abstract val bucket: BucketName
    private val clock = Clock.systemUTC()

    protected val s3Bucket by lazy {
        S3Bucket.Http(bucket, aws.region, { aws.credentials }, http, clock)
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
            assertThat(s3Bucket.headObject(key).successValue(), equalTo(Unit))

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

    @Test
    fun `pre signed request`() {
        val preSigner = S3BucketPreSigner(
            bucketName = bucket,
            region = aws.region,
            credentials = aws.credentials,
            clock = clock
        )
        val http = SetXForwardedHost().then(http)

        waitForBucketCreation()
        try {
            preSigner.put(
                key = key,
                headers = listOf("content-type" to "text/plain"),
                duration = Duration.ofMinutes(1)
            ).also {
                val response = Request(PUT, it.uri)
                    .headers(it.signedHeaders)
                    .body("hello there")
                    .let(http)
                assertThat(response, hasStatus(CREATED) or hasStatus(OK))
            }

            preSigner.get(key, Duration.ofMinutes(1)).also {
                val response = Request(GET, it.uri)
                    .headers(it.signedHeaders)
                    .let(http)
                assertThat(response, hasStatus(OK) and hasBody("hello there"))
            }

            preSigner.delete(key, Duration.ofMinutes(1)).also {
                val response = Request(DELETE, it.uri)
                    .headers(it.signedHeaders)
                    .let(http)
                assertThat(response, hasStatus(OK) or hasStatus(NO_CONTENT))
            }
        } finally {
            s3Bucket.deleteObject(key)
            s3Bucket.deleteBucket()
        }
    }

    @Test
    fun `tag lifecycle`() {
        waitForBucketCreation()

        try {
            s3Bucket.putObjectTagging(key, listOf(Tag("foo", "bar"))).errorValue {
                assertThat(it.status, equalTo(NOT_FOUND))
                assertThat(it.message!!, containsSubstring("NoSuchKey"))
            }
            s3Bucket.deleteObjectTagging(key).errorValue {
                assertThat(it.status, equalTo(NOT_FOUND))
                assertThat(it.message!!, containsSubstring("NoSuchKey"))
            }
            s3Bucket.getObjectTagging(key).errorValue {
                assertThat(it.status, equalTo(NOT_FOUND))
                assertThat(it.message!!, containsSubstring("NoSuchKey"))
            }

            s3Bucket.putObject(
                key = key,
                content = "hello there".byteInputStream(),
                tags = listOf(Tag("hello", "there"))
            ).successValue()
            assertThat(
                s3Bucket.getObjectTagging(key).successValue(),
                equalTo(listOf(Tag("hello", "there")))
            )

            s3Bucket.putObjectTagging(key, listOf(Tag("foo", "bar"))).successValue()
            assertThat(
                s3Bucket.getObjectTagging(key).successValue(),
                equalTo(listOf(Tag("foo", "bar")))
            )

            s3Bucket.deleteObjectTagging(key).successValue()
            assertThat(
                s3Bucket.getObjectTagging(key).successValue(),
                equalTo(emptyList())
            )
        } finally {
            s3Bucket.deleteObject(key)
            s3Bucket.deleteBucket()
        }
    }

    open fun waitForBucketCreation() {}
}
