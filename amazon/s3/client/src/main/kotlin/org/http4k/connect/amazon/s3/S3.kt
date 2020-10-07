package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.Result
import org.http4k.core.Status
import org.http4k.core.Uri
import java.io.InputStream

data class RemoteFailure(val uri: Uri, val status: Status)


/**
 * Interface for global S3 operations
 */
interface S3 {
    fun buckets(): Result<List<BucketName>, RemoteFailure>
    fun create(bucketName: BucketName): Result<Unit, RemoteFailure>
    fun delete(bucketName: BucketName): Result<Unit?, RemoteFailure>

    /**
     * Interface for bucket-specific S3 operations
     */
    interface Bucket {
        fun create(): Result<Unit, RemoteFailure>
        fun delete(): Result<Unit?, RemoteFailure>
        fun delete(key: BucketKey): Result<Unit?, RemoteFailure>
        fun get(key: BucketKey): Result<InputStream?, RemoteFailure>
        fun set(key: BucketKey, content: InputStream): Result<Unit, RemoteFailure>
        fun copy(originalKey: BucketKey, newKey: BucketKey): Result<Unit, RemoteFailure>
        fun list(): Result<List<BucketKey>, RemoteFailure>

        companion object
    }

    companion object
}

