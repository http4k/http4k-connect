package org.http4k.connect.amazon.kms

import org.http4k.connect.amazon.AwsContract
import org.http4k.core.HttpHandler

abstract class KMSContract(http: HttpHandler) : AwsContract(http) {
    private val kms by lazy {
        KMS.Http(aws.scope, { aws.credentials }, http)
    }
}
