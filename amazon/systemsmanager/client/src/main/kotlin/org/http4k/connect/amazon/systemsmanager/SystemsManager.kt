package org.http4k.connect.amazon.systemsmanager

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/systems-manager/latest/APIReference/Welcome.html
 */
interface SystemsManager {
    operator fun invoke(request: PutParameter): Result<PutParameterResult, RemoteFailure>
    operator fun invoke(request: GetParameter): Result<ParameterValue, RemoteFailure>
    operator fun invoke(request: DeleteParameter): Result<Unit, RemoteFailure>

    companion object
}

