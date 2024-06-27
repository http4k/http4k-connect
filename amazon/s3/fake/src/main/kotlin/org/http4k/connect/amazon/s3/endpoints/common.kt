package org.http4k.connect.amazon.s3.endpoints

import org.http4k.connect.amazon.s3.BucketKeyContent
import org.http4k.connect.amazon.s3.S3Error
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.APPLICATION_XML
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.FORBIDDEN
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.with
import org.http4k.routing.asRouter
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel


fun Request.subdomain(buckets: Storage<Unit>): String =
    (header("x-forwarded-host") ?: header("host") ?: uri.host)
        .split('.')
        .firstOrNull()
        ?: run {
            buckets[GLOBAL_BUCKET] = Unit
            GLOBAL_BUCKET
        }

val s3ErrorLens by lazy {
    Body.viewModel(HandlebarsTemplates().CachingClasspath(), APPLICATION_XML).toLens()
}

const val GLOBAL_BUCKET = "unknown"

fun invalidBucketNameResponse() = Response(NOT_FOUND)
    .with(s3ErrorLens of S3Error("NoSuchBucket", message = "The resource you requested does not exist"))

fun invalidBucketKeyResponse() = Response(NOT_FOUND)
    .with(s3ErrorLens of S3Error("NoSuchKey", message = "The resource you requested does not exist"))

fun invalidObjectStateResponse() = Response(FORBIDDEN)
    .with(s3ErrorLens of S3Error("InvalidObjectState", message = "Object is in an invalid state"))

internal val excludedObjectHeaders = setOf(
    "authorization",
    "x-forwarded-host",
    "x-amz-content-sha256",
    "x-amz-date"
)

internal fun getHeadersWithoutXHttp4kPrefix(it: BucketKeyContent) =
    it.headers.map { it.first.removePrefix("x-http4k-") to it.second }

// TODO may be overlooked that `queries` router only passes if the query has a value
fun queryPresent(name: String) = { req: Request -> req.queries(name).isNotEmpty() }.asRouter("Query present: $name")
