package org.http4k.connect.amazon.systemsmanager

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/systems-manager/latest/APIReference/Welcome.html
 */
interface SystemsManager {
    fun put(request: PutParameterRequest): Result<PutParameterResponse, RemoteFailure>
    fun get(request: GetParameterRequest): Result<GetParameterResponse, RemoteFailure>
    fun delete(request: DeleteParameterRequest): Result<Unit, RemoteFailure>

    companion object
}

