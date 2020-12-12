package org.http4k.connect.amazon.secretsmanager

import dev.forkhandles.result4k.Result
import org.http4k.connect.RemoteFailure

/**
 * Docs: https://docs.aws.amazon.com/secretsmanager/latest/apireference/Welcome.html
 */
interface SecretsManager {
    operator fun invoke(request: CreateSecret): Result<CreatedSecret, RemoteFailure>
    operator fun invoke(request: DeleteSecret): Result<DeletedSecret, RemoteFailure>
    operator fun invoke(request: GetSecretValue): Result<SecretValue, RemoteFailure>
    operator fun invoke(request: ListSecrets): Result<Secrets, RemoteFailure>
    operator fun invoke(request: PutSecretValue): Result<UpdatedSecretValue, RemoteFailure>
    operator fun invoke(request: UpdateSecret): Result<UpdatedSecret, RemoteFailure>

    companion object
}

