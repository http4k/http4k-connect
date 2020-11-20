package org.http4k.connect.amazon.secretsmanager

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

interface SecretsManager {
    fun get(request: GetSecretValue.Request): Result<GetSecretValue.Response?, RemoteFailure>
    fun put(request: PutSecretValue.Request): Result<PutSecretValue.Response, RemoteFailure>

    companion object
}

