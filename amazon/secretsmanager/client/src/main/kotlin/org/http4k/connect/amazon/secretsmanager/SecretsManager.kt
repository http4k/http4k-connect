package org.http4k.connect.amazon.secretsmanager

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/secretsmanager/latest/apireference/Welcome.html
 */
interface SecretsManager {
    fun create(request: CreateSecret.Request): Result<CreateSecret.Response, RemoteFailure>
    fun delete(request: DeleteSecret.Request): Result<DeleteSecret.Response, RemoteFailure>
    fun get(request: GetSecretValue.Request): Result<GetSecretValue.Response, RemoteFailure>
    fun list(request: ListSecrets.Request): Result<ListSecrets.Response, RemoteFailure>
    fun put(request: PutSecretValue.Request): Result<PutSecretValue.Response, RemoteFailure>
    fun update(request: UpdateSecret.Request): Result<UpdateSecret.Response, RemoteFailure>

    companion object
}

