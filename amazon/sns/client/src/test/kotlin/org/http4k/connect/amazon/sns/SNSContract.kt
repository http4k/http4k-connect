package org.http4k.connect.amazon.sns

import org.http4k.connect.amazon.AwsContract
import org.http4k.core.HttpHandler

abstract class SNSContract(http: HttpHandler) : AwsContract(http) {
    private val sns by lazy {
        SNS.Http(aws.region, { aws.credentials }, http)
    }
}
