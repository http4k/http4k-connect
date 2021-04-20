package org.http4k.connect.amazon.s3.endpoints

import org.http4k.connect.amazon.s3.S3Error
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.APPLICATION_XML
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.with
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.viewModel


fun Request.subdomain(buckets: Storage<Unit>): String =
    (header("x-forwarded-host") ?: header("host"))
        ?.also { System.err.print("SUBDOMAIN: $it") }
        ?.split('.')
        ?.firstOrNull().also { System.err.print("  is $it\n") }
        ?: run {
            buckets[GLOBAL_BUCKET] = Unit
            GLOBAL_BUCKET
        }

val lens by lazy {
    Body.viewModel(HandlebarsTemplates().CachingClasspath(), APPLICATION_XML).toLens()
}

const val GLOBAL_BUCKET = "unknown"

fun invalidBucketNameResponse() = Response(NOT_FOUND).with(lens of S3Error("NoSuchBucket"))
