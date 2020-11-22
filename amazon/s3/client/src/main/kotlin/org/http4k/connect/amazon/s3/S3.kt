package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.Result
import org.http4k.connect.Listing
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.BucketName
import java.io.InputStream

/**
 * Docs: https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html
 */
interface S3 {
    fun buckets(): Result<Listing<BucketName>, RemoteFailure>
    fun create(bucketName: BucketName): Result<Unit, RemoteFailure>
    fun delete(bucketName: BucketName): Result<Unit?, RemoteFailure>

    /**
     * Interface for bucket-specific S3 operations
     */
    interface Bucket {
        fun create(): Result<Unit, RemoteFailure>
        fun delete(): Result<Unit?, RemoteFailure>
        fun delete(key: BucketKey): Result<Unit?, RemoteFailure>
        operator fun get(key: BucketKey): Result<InputStream?, RemoteFailure>
        operator fun set(key: BucketKey, content: InputStream): Result<Unit, RemoteFailure>
        fun copy(originalKey: BucketKey, newKey: BucketKey): Result<Unit, RemoteFailure>

        /**
         * List items in a bucket. Note that the S3 API maxes out at 1000 items.
         */
        fun list(): Result<Listing<BucketKey>, RemoteFailure>

        companion object
    }

    companion object
}

