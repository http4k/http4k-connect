package org.http4k.connect.google.analytics

import org.http4k.connect.FakeSystemContract
import org.http4k.connect.amazon.s3.FakeS3Bucket
import org.http4k.core.Method.GET
import org.http4k.core.Request

class FakeS3BucketChaosTest : FakeSystemContract(FakeS3Bucket()) {
    override val anyValidRequest = Request(GET, "/")
}
