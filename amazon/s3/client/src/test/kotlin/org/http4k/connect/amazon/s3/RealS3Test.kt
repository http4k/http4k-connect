package org.http4k.connect.amazon.s3

import org.http4k.client.JavaHttpClient
import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.core.then
import org.http4k.filter.ClientFilters
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

interface S3Contract {
    val http: HttpHandler

    @Test
    fun `lifecycle`() {

    }
}

@Disabled
class RealS3Test : S3Contract {
    override val http = ClientFilters.SetBaseUriFrom(Uri.of("http://localhost:9876")).then(JavaHttpClient())
}
