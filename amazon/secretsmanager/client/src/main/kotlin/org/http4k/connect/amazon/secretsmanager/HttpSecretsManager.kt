package org.http4k.connect.amazon.secretsmanager

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.AmazonJsonApi
import org.http4k.connect.amazon.model.AwsService
import org.http4k.core.HttpHandler
import org.http4k.filter.Payload
import java.time.Clock

fun SecretsManager.Companion.Http(scope: AwsCredentialScope,
                                  credentialsProvider: () -> AwsCredentials,
                                  rawHttp: HttpHandler = JavaHttpClient(),
                                  clock: Clock = Clock.systemDefaultZone(),
                                  payloadMode: Payload.Mode = Payload.Mode.Signed) = object : SecretsManager {
    private val awsService = AwsService.of("secretsmanager")

    private val api = AmazonJsonApi(awsService, scope, credentialsProvider, SecretsManagerJackson, rawHttp, clock, payloadMode)

    override fun create(request: CreateSecret.Request) =
        api.required<CreateSecret.Request, CreateSecret.Response>("CreateSecret", request)

    override fun delete(request: DeleteSecret.Request) =
        api.optional<DeleteSecret.Request, DeleteSecret.Response>("DeleteSecret", request)

    override fun get(request: GetSecret.Request) =
        api.optional<GetSecret.Request, GetSecret.Response>("GetSecretValue", request)

    override fun list(request: ListSecrets.Request) =
        api.required<ListSecrets.Request, ListSecrets.Response>("ListSecrets", request)

    override fun put(request: PutSecret.Request) =
        api.optional<PutSecret.Request, PutSecret.Response>("PutSecretValue", request)

    override fun update(request: UpdateSecret.Request) =
        api.optional<UpdateSecret.Request, UpdateSecret.Response>("UpdateSecret", request)
}
