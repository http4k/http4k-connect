package org.http4k.connect.amazon.s3

import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.BucketName
import java.io.InputStream

class ListBuckets

data class CreateBucket(val bucketName: BucketName)

data class DeleteBucket(val bucketName: BucketName)

class Create
class Delete
class ListKeys
data class CopyKey(val source: BucketKey, val destination: BucketKey)
data class DeleteKey(val key: BucketKey)
data class GetKey(val key: BucketKey)
data class PutKey(val key: BucketKey, val content: InputStream)
