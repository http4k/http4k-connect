package org.http4k.connect.amazon.systemsmanager

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/systems-manager/latest/APIReference/Welcome.html
 */
interface SystemsManager {
    fun put(request: PutParameter.Request): Result<PutParameter.Response, RemoteFailure>
    fun get(request: GetParameter.Request): Result<GetParameter.Response, RemoteFailure>
    fun delete(request: DeleteParameter.Request): Result<Unit, RemoteFailure>

    companion object
}

