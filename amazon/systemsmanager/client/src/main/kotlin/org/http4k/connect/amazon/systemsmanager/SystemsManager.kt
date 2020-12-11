package org.http4k.connect.amazon.systemsmanager

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/systems-manager/latest/APIReference/Welcome.html
 */
interface SystemsManager {
    operator fun invoke(request: PutParameterRequest): Result<PutParameterResponse, RemoteFailure>
    operator fun invoke(request: GetParameterRequest): Result<GetParameterResponse, RemoteFailure>
    operator fun invoke(request: DeleteParameterRequest): Result<Unit, RemoteFailure>

    companion object
}

