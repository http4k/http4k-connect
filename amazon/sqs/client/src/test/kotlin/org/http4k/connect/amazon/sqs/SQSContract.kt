package org.http4k.connect.amazon.sqs

import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsService
import org.http4k.core.HttpHandler
import org.http4k.filter.debug

abstract class SQSContract(http: HttpHandler) : AwsContract(AwsService.of("sqs"), http) {

    private val sqs by lazy {
        SQS.Http(aws.scope, { aws.credentials }, http.debug())
    }
}
