package org.http4k.connect.amazon.secretsmanager

import org.http4k.connect.amazon.AwsContract
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.systemsmanager.Http
import org.http4k.connect.amazon.systemsmanager.SystemsManager
import org.http4k.core.HttpHandler

abstract class SystemsManagerContract(http: HttpHandler) : AwsContract(AwsService.of("secretsmanager"), http) {
    private val sm by lazy {
        SystemsManager.Http(aws.scope, { aws.credentials }, http)
    }
}
