package org.http4k.connect.amazon.s3

import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.BucketName
import org.http4k.template.ViewModel
import java.time.ZonedDateTime

data class ListAllMyBuckets(val buckets: List<BucketName>) : ViewModel

data class BucketKeyContent(val key: BucketKey,
                            val content: String,
                            val modified: ZonedDateTime) {
    val size = content.length
}

data class ListBucketResult(val keys: List<BucketKeyContent>) : ViewModel {
    val keyCount = keys.size
    val maxKeys = Integer.MAX_VALUE
}
