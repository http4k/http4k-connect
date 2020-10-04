package org.http4k.connect.amazon.s3

import com.natpryce.hamkrest.absent
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

abstract class S3BucketContract(private val http: HttpHandler) {
    abstract val aws: AwsEnvironment

    private val s3Bucket by lazy {
        S3.Bucket.Http(aws.uri, PrintRequestAndResponse().then(http), aws.scope, { aws.credentials })
    }

    private val key = BucketKey(UUID.randomUUID().toString())

    @BeforeEach
    fun cleanup() {
        s3Bucket.delete(key).successValue()
    }

    @Test
    fun `bucket key lifecycle`() {
        with(s3Bucket) {
            assertThat(list().successValue().contains(key), equalTo(false))
            assertThat(get(key).successValue(), absent())
            assertThat(set(key, "hello".byteInputStream()), equalTo(Success(Unit)))
            assertThat(get(key).successValue().let { String(it!!.readBytes()) }, equalTo("hello"))
            assertThat(list().successValue().contains(key), equalTo(true))
            assertThat(set(key, "there".byteInputStream()), equalTo(Success(Unit)))
            assertThat(get(key).successValue().let { String(it!!.readBytes()) }, equalTo("there"))
            assertThat(delete(key), equalTo(Success(Unit)))
            assertThat(get(key).successValue(), absent())
            assertThat(list().successValue().contains(key), equalTo(false))
        }
    }
}
