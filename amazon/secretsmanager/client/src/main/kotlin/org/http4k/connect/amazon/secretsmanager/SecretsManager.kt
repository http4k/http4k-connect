package org.http4k.connect.amazon.secretsmanager

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/secretsmanager/latest/apireference/Welcome.html
 */
interface SecretsManager {
    operator fun invoke(request: CreateSecretRequest): Result<CreateSecretResponse, RemoteFailure>
    operator fun invoke(request: DeleteSecretRequest): Result<DeleteSecretResponse, RemoteFailure>
    operator fun invoke(request: GetSecretValueRequest): Result<GetSecretValueResponse, RemoteFailure>
    operator fun invoke(request: ListSecretsRequest): Result<ListSecretsResponse, RemoteFailure>
    operator fun invoke(request: PutSecretValueRequest): Result<PutSecretValueResponse, RemoteFailure>
    operator fun invoke(request: UpdateSecretRequest): Result<UpdateSecretResponse, RemoteFailure>

    companion object
}

