package org.http4k.connect.amazon.cloudfront

import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.cloudfront.model.DistributionId
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.junit.jupiter.api.Test

abstract class CloudFrontContract : AwsContract {
    abstract val http: HttpHandler

    private val cloudFront by lazy {
        CloudFront.Http({ aws.credentials }, http)
    }

    private val distribution = DistributionId.of("E1HHLORGLBAQYP")

    @Test
    fun `invalidate cache`() {
        cloudFront.createInvalidation(distribution, "/foobar").successValue()
    }
}

