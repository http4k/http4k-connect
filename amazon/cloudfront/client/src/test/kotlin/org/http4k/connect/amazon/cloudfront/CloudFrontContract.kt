package org.http4k.connect.amazon.cloudfront

import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.DistributionId
import org.http4k.connect.successValue
import org.http4k.core.HttpHandler
import org.http4k.filter.debug
import org.junit.jupiter.api.Test

abstract class CloudFrontContract(http: HttpHandler) : AwsContract(http) {
    private val cloudFront by lazy {
        CloudFront.Http({ aws.credentials }, http.debug())
    }

    private val distribution = DistributionId.of("E1HHLORGLBAQYP")

    @Test
    fun `invalidate cache`() {
        cloudFront.createInvalidation(distribution, "/foobar").successValue()
    }
}

