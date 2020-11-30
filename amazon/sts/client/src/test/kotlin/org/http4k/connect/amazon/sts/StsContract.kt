package org.http4k.connect.amazon.sts

import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsService
import org.http4k.core.HttpHandler

abstract class StsContract(http: HttpHandler): AwsContract(AwsService.of("sts"), http) {
}
