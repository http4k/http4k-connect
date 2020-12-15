package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.s3.action.GetKey
import org.http4k.connect.amazon.s3.action.PutKey
import org.http4k.connect.amazon.s3.action.S3Action
import org.http4k.connect.amazon.s3.action.S3BucketAction
import java.io.InputStream

/**
 * Docs: https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html
 */
interface S3 {
    /**
     * Available actions:
     *  CreateBucket
     *  DeleteBucket
     *  ListBuckets
     */
    operator fun <R> invoke(request: S3Action<R>): Result<R, RemoteFailure>

    /**
     * Interface for bucket-specific S3 operations
     */
    interface Bucket {
        /**
         * Available actions:
         *  CopyKey
         *  Create (bucket)
         *  CreateKey
         *  Delete (bucket)
         *  DeleteKey
         *  GetKey
         *  ListKeys
         *  PutKey
         */
        operator fun <R> invoke(request: S3BucketAction<R>): Result<R, RemoteFailure>

        operator fun get(key: BucketKey): Result<InputStream?, RemoteFailure> = this(GetKey(key))
        operator fun set(key: BucketKey, content: InputStream): Result<Unit, RemoteFailure> = this(PutKey(key, content))

        companion object
    }

    companion object
}
