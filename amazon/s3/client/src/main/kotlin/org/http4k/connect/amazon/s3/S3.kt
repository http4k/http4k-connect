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
    operator fun invoke(request: ListBuckets): Result<Listing<BucketName>, RemoteFailure>
    operator fun invoke(request: CreateBucket): Result<Unit, RemoteFailure>
    operator fun invoke(request: DeleteBucket): Result<Unit?, RemoteFailure>

    /**
     * Interface for bucket-specific S3 operations
     */
    interface Bucket {
        /**
         * List items in a bucket. Note that the S3 API maxes out at 1000 items.
         */
        operator fun invoke(request: ListKeys): Result<Listing<BucketKey>, RemoteFailure>
        operator fun invoke(request: Create): Result<Unit, RemoteFailure>
        operator fun invoke(request: Delete): Result<Unit?, RemoteFailure>
        operator fun invoke(request: DeleteKey): Result<Unit?, RemoteFailure>
        operator fun invoke(request: GetKey): Result<InputStream?, RemoteFailure>
        operator fun invoke(request: PutKey): Result<Unit, RemoteFailure>
        operator fun invoke(request: CopyKey): Result<Unit, RemoteFailure>
        operator fun get(key: BucketKey): Result<InputStream?, RemoteFailure> = this(GetKey(key))
        operator fun set(key: BucketKey, content: InputStream): Result<Unit, RemoteFailure> = this(PutKey(key, content))

        companion object
    }

    companion object
}

