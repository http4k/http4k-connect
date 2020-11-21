package org.http4k.connect.amazon.secretsmanager

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

interface SecretsManager {
    fun lookup(request: GetSecret.Request): Result<GetSecret.Response?, RemoteFailure>
    fun create(request: CreateSecret.Request): Result<CreateSecret.Response, RemoteFailure>
    fun update(request: UpdateSecret.Request): Result<UpdateSecret.Response?, RemoteFailure>
    fun delete(request: DeleteSecret.Request): Result<DeleteSecret.Response?, RemoteFailure>

    companion object
}

