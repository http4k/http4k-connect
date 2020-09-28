package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.Result
import org.http4k.core.Status
import java.io.InputStream

data class RemoteFailure(val status: Status)
data class BucketName(val name: String)
data class BucketKey(val value: String)

interface S3 {
    fun bucket(bucketName: BucketName): Result<Bucket, RemoteFailure>

    fun buckets(): Result<Iterable<BucketName>, RemoteFailure>

    interface Bucket {
        fun create(): Result<Unit, RemoteFailure>
        fun delete(): Result<Unit, RemoteFailure>
        fun delete(key: BucketKey): Result<Unit, RemoteFailure>
        fun delete(keys: Iterable<BucketKey>): Result<Unit, RemoteFailure>
        fun set(key: BucketKey, content: InputStream): Result<Unit, RemoteFailure>
        fun get(key: BucketKey): Result<InputStream?, RemoteFailure>
        fun list(): Result<BucketKey, RemoteFailure>

        companion object
    }

    companion object
}

