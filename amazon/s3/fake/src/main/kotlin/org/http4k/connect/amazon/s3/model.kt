package org.http4k.connect.amazon.s3

import org.http4k.connect.amazon.s3.model.BucketKey
import org.http4k.connect.amazon.s3.model.BucketName
import org.http4k.core.Headers
import org.http4k.template.ViewModel
import java.time.ZonedDateTime

data class ListAllMyBuckets(val buckets: List<BucketName>) : ViewModel

data class BucketKeyContent(
    val key: BucketKey,
    val content: String,
    val modified: ZonedDateTime,
    val headers: Headers
) {
    val size = content.length
}

data class ListBucketResult(val bucketName: String, val keys: List<BucketKeyContent>) : ViewModel {
    val keyCount = keys.size
    val maxKeys = Integer.MAX_VALUE
}

data class S3Error(val code: String, val resource: String? = "") : ViewModel
