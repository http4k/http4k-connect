package org.http4k.connect.amazon.sqs

import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.sns.Http
import org.http4k.connect.amazon.sns.SNS
import org.http4k.core.HttpHandler

abstract class SNSContract(http: HttpHandler) : AwsContract(http) {
    private val sns by lazy {
        SNS.Http(aws.region, { aws.credentials }, http)
    }
}
