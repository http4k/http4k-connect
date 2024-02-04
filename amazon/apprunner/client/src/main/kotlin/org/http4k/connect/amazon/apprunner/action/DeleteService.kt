package org.http4k.connect.amazon.apprunner.action

import org.http4k.connect.Http4kConnectAction
import org.http4k.connect.amazon.apprunner.AppRunnerAction
import org.http4k.connect.amazon.apprunner.model.AppRunnerService
import org.http4k.connect.amazon.core.model.ARN
import org.http4k.connect.kClass

@Http4kConnectAction
data class DeleteService(val ServiceArn: ARN) : AppRunnerAction<AppRunnerService>(kClass())
