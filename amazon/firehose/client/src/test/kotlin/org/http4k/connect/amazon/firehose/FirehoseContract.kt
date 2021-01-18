package org.http4k.connect.amazon.firehose

import org.http4k.connect.amazon.AwsContract
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Test

abstract class FirehoseContract(http: HttpHandler) : AwsContract(http) {
    private val firehose by lazy {
        Firehose.Http(aws.region, { aws.credentials }, http)
    }

    @Test
    fun `send records`() {

    }
}
