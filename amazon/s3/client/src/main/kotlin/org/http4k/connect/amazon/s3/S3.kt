package org.http4k.connect.amazon.s3

import dev.forkhandles.result4k.Result
import org.http4k.connect.Action
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.model.BucketKey
import org.http4k.connect.amazon.model.Region
import org.http4k.core.Request
import org.http4k.core.Response
import org.xml.sax.InputSource
import java.io.InputStream
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Docs: https://docs.aws.amazon.com/AmazonS3/latest/API/Welcome.html
 */
interface S3Action<R> : Action<R>

interface S3BucketAction<R> {
    fun toRequest(region: Region): Request
    fun toResult(response: Response): Result<R, RemoteFailure>
}

interface S3 {
    operator fun <R> invoke(request: S3Action<R>): Result<R, RemoteFailure>

    /**
     * Interface for bucket-specific S3 operations
     */
    interface Bucket {
        operator fun <R> invoke(request: S3BucketAction<R>): Result<R, RemoteFailure>
        operator fun get(key: BucketKey): Result<InputStream?, RemoteFailure> = this(GetKey(key))
        operator fun set(key: BucketKey, content: InputStream): Result<Unit, RemoteFailure> = this(PutKey(key, content))

        companion object
    }

    companion object
}

internal val documentBuilderFactory by lazy {
    DocumentBuilderFactory.newInstance()
        .newDocumentBuilder()
        .apply { setEntityResolver { _, _ -> InputSource(StringReader("")) } }
}
