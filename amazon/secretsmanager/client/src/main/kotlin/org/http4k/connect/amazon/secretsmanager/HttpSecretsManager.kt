package org.http4k.connect.amazon.secretsmanager

import dev.forkhandles.result4k.Result
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AmazonJsonApi
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.optional
import org.http4k.connect.amazon.required
import org.http4k.core.HttpHandler
import org.http4k.filter.Payload
import java.time.Clock

fun SecretsManager.Companion.Http(scope: AwsCredentialScope,
                                  credentialsProvider: () -> AwsCredentials,
                                  rawHttp: HttpHandler = JavaHttpClient(),
                                  clock: Clock = Clock.systemDefaultZone(),
                                  payloadMode: Payload.Mode = Payload.Mode.Signed) = object : SecretsManager {
    private val api = AmazonJsonApi(AwsService.of("secretsmanager"), SecretsManagerJackson, scope, credentialsProvider, rawHttp, clock, payloadMode)

    override fun create(request: CreateSecret.Request): Result<CreateSecret.Response, RemoteFailure> =
        api.required("CreateSecret", request)

    override fun delete(request: DeleteSecret.Request): Result<DeleteSecret.Response?, RemoteFailure> =
        api.optional("DeleteSecret", request)

    override fun get(request: GetSecret.Request): Result<GetSecret.Response?, RemoteFailure> =
        api.optional("GetSecretValue", request)

    override fun list(request: ListSecrets.Request): Result<ListSecrets.Response, RemoteFailure> =
        api.required("ListSecrets", request)

    override fun put(request: PutSecret.Request): Result<PutSecret.Response?, RemoteFailure> =
        api.optional("PutSecretValue", request)

    override fun update(request: UpdateSecret.Request): Result<UpdateSecret.Response?, RemoteFailure> =
        api.optional("UpdateSecret", request)
}
