package org.http4k.connect.amazon.s3

import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.BucketName
import java.io.InputStream

class ListBucketsRequest

data class CreateBucketRequest(val bucketName: BucketName)

data class DeleteBucketRequest(val bucketName: BucketName)

class CreateRequest
class DeleteRequest
class ListKeysRequest
data class CopyKeyRequest(val source: BucketKey, val destination: BucketKey)
data class DeleteKeyRequest(val key: BucketKey)
data class GetKeyRequest(val key: BucketKey)
data class PutKeyRequest(val key: BucketKey, val content: InputStream)
