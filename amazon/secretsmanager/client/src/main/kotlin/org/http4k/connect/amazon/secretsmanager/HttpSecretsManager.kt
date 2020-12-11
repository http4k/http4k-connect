package org.http4k.connect.amazon.secretsmanager

import dev.forkhandles.result4k.Result
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AmazonJsonApi
import org.http4k.connect.amazon.invoke
import org.http4k.connect.amazon.model.AwsService
import org.http4k.core.HttpHandler
import org.http4k.filter.Payload
import java.time.Clock

fun SecretsManager.Companion.Http(scope: AwsCredentialScope,
                                  credentialsProvider: () -> AwsCredentials,
                                  rawHttp: HttpHandler = JavaHttpClient(),
                                  clock: Clock = Clock.systemDefaultZone(),
                                  payloadMode: Payload.Mode = Payload.Mode.Signed) = object : SecretsManager {
    private val api = AmazonJsonApi(AwsService.of("secretsmanager"), SecretsManagerMoshi, scope, credentialsProvider, rawHttp, clock, payloadMode)

    override fun create(request: CreateSecretRequest): Result<CreateSecretResponse, RemoteFailure> =
        api("CreateSecret", request)

    override fun delete(request: DeleteSecretRequest): Result<DeleteSecretResponse, RemoteFailure> =
        api("DeleteSecret", request)

    override fun get(request: GetSecretValueRequest): Result<GetSecretValueResponse, RemoteFailure> =
        api("GetSecretValue", request)

    override fun list(request: ListSecretsRequest): Result<ListSecretsResponse, RemoteFailure> =
        api("ListSecrets", request)

    override fun put(request: PutSecretValueRequest): Result<PutSecretValueResponse, RemoteFailure> =
        api("PutSecretValue", request)

    override fun update(request: UpdateSecretRequest): Result<UpdateSecretResponse, RemoteFailure> =
        api("UpdateSecret", request)
}
