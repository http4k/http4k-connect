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
    operator fun invoke(request: ListBucketsRequest): Result<Listing<BucketName>, RemoteFailure>
    operator fun invoke(request: CreateBucketRequest): Result<Unit, RemoteFailure>
    operator fun invoke(request: DeleteBucketRequest): Result<Unit?, RemoteFailure>

    /**
     * Interface for bucket-specific S3 operations
     */
    interface Bucket {
        /**
         * List items in a bucket. Note that the S3 API maxes out at 1000 items.
         */
        operator fun invoke(request: ListKeysRequest): Result<Listing<BucketKey>, RemoteFailure>
        operator fun invoke(request: CreateRequest): Result<Unit, RemoteFailure>
        operator fun invoke(request: DeleteRequest): Result<Unit?, RemoteFailure>
        operator fun invoke(request: DeleteKeyRequest): Result<Unit?, RemoteFailure>
        operator fun invoke(request: GetKeyRequest): Result<InputStream?, RemoteFailure>
        operator fun invoke(request: PutKeyRequest): Result<Unit, RemoteFailure>
        operator fun invoke(request: CopyKeyRequest): Result<Unit, RemoteFailure>
        operator fun get(key: BucketKey): Result<InputStream?, RemoteFailure> = this(GetKeyRequest(key))
        operator fun set(key: BucketKey, content: InputStream): Result<Unit, RemoteFailure> = this(PutKeyRequest(key, content))

        companion object
    }

    companion object
}

