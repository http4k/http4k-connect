package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.Result
import org.http4k.core.Status
import org.http4k.core.Uri
import java.io.InputStream

data class RemoteFailure(val uri: Uri, val status: Status)

data class BucketName(val name: String) {
    override fun toString() = name
}

data class BucketKey(val value: String) {
    override fun toString() = value
}

interface S3 {
    fun buckets(): Result<Iterable<BucketName>, RemoteFailure>
    fun create(bucketName: BucketName): Result<Unit, RemoteFailure>
    fun delete(bucketName: BucketName): Result<Unit, RemoteFailure>

    interface Bucket {
        fun delete(key: BucketKey): Result<Unit, RemoteFailure>
        fun set(key: BucketKey, content: InputStream): Result<Unit, RemoteFailure>
        fun get(key: BucketKey): Result<InputStream?, RemoteFailure>
        fun list(): Result<List<BucketKey>, RemoteFailure>

        companion object
    }

    companion object
}

