package org.http4k.connect.amazon.s3

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters.PrintRequestAndResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

abstract class S3BucketContract {
    abstract val http: HttpHandler
    abstract val uri: Uri
    abstract val credentials: AwsCredentials
    abstract val scope: AwsCredentialScope

    private val s3Bucket by lazy {
        S3.Bucket.Http(uri, PrintRequestAndResponse().then(http), scope, { credentials })
    }

    private val key = BucketKey(UUID.randomUUID().toString())

    @BeforeEach
    fun cleanup() {
        s3Bucket.delete(key).successValue()
    }

    @Test
    fun `bucket key lifecycle`() {
        assertThat(s3Bucket.list().successValue().contains(key), equalTo(false))
        assertThat(s3Bucket.get(key).successValue(), absent())
        assertThat(s3Bucket.set(key, "hello".byteInputStream()), equalTo(Success(Unit)))
        assertThat(s3Bucket.get(key).successValue().let { String(it!!.readBytes()) }, equalTo("hello"))
        assertThat(s3Bucket.list().successValue().contains(key), equalTo(true))
        assertThat(s3Bucket.set(key, "there".byteInputStream()), equalTo(Success(Unit)))
        assertThat(s3Bucket.get(key).successValue().let { String(it!!.readBytes()) }, equalTo("there"))
        assertThat(s3Bucket.delete(key), equalTo(Success(Unit)))
        assertThat(s3Bucket.get(key).successValue(), absent())
        assertThat(s3Bucket.list().successValue().contains(key), equalTo(false))
    }
}
