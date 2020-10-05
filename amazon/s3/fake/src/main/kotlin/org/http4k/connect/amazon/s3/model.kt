package org.http4k.connect.amazon.s3

import org.http4k.template.ViewModel
import java.time.Instant

data class ListAllMyBuckets(val buckets: List<BucketName>) : ViewModel

data class BucketKeyContent(val key: BucketKey, val content: ByteArray, val modified: Instant) {
    val size = content.size
}

data class ListBucketResult(val keys: List<BucketKeyContent>) : ViewModel {
    val keyCount = keys.size
    val maxKeys = Integer.MAX_VALUE
}
