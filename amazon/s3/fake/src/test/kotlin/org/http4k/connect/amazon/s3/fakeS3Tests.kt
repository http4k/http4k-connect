package org.http4k.connect.amazon.s3

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.connect.amazon.core.model.Timestamp
import org.http4k.connect.amazon.fakeAwsEnvironment
import org.http4k.connect.amazon.s3.TestingHeaders.X_HTTP4K_LAST_MODIFIED
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.connect.successValue
import org.http4k.lens.LastModified
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.UUID

class FakeS3BucketTest : S3BucketContract(FakeS3()) {
    override val aws = fakeAwsEnvironment
    override val bucket = BucketName.of(UUID.randomUUID().toString())

    @Test
    fun `can set last-modified on an object`() {
        try {
            val lastModifiedDate = ZonedDateTime.of(LocalDate.EPOCH, LocalTime.MIDNIGHT, ZoneId.of("GMT"))

            assertThat(
                s3Bucket.putObject(
                    key, "hello".byteInputStream(), listOf(
                        X_HTTP4K_LAST_MODIFIED to LastModified.of(lastModifiedDate).toHeaderValue()
                    )
                ).successValue(), equalTo(Unit)
            )

            assertThat(
                s3Bucket.listObjectsV2().successValue().items.first().LastModified, equalTo(
                    Timestamp.of(lastModifiedDate.toEpochSecond())
                )
            )
        } finally {
            s3Bucket.deleteObject(key)
            s3Bucket.deleteBucket()
        }
    }

}

class FakeS3BucketPathStyleTest : S3BucketContract(FakeS3()) {
    override val aws = fakeAwsEnvironment
    override val bucket = BucketName.of(UUID.randomUUID().toString().replace('-', '.'))
}

class FakeS3GlobalTest : S3GlobalContract(FakeS3()) {
    override val aws = fakeAwsEnvironment
}
