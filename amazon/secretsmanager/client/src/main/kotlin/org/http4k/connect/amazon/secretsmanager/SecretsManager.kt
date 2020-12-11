package org.http4k.connect.amazon.secretsmanager

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/secretsmanager/latest/apireference/Welcome.html
 */
interface SecretsManager {
    fun create(request: CreateSecretRequest): Result<CreateSecretResponse, RemoteFailure>
    fun delete(request: DeleteSecretRequest): Result<DeleteSecretResponse, RemoteFailure>
    fun get(request: GetSecretValueRequest): Result<GetSecretValueResponse, RemoteFailure>
    fun list(request: ListSecretsRequest): Result<ListSecretsResponse, RemoteFailure>
    fun put(request: PutSecretValueRequest): Result<PutSecretValueResponse, RemoteFailure>
    fun update(request: UpdateSecretRequest): Result<UpdateSecretResponse, RemoteFailure>

    companion object
}

