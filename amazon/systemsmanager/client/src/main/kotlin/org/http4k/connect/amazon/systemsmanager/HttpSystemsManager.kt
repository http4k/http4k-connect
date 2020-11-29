package org.http4k.connect.amazon.systemsmanager

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

fun SystemsManager.Companion.Http(scope: AwsCredentialScope,
                                  credentialsProvider: () -> AwsCredentials,
                                  rawHttp: HttpHandler = JavaHttpClient(),
                                  clock: Clock = Clock.systemDefaultZone(),
                                  payloadMode: Payload.Mode = Payload.Mode.Signed) = object : SystemsManager {
    private val api = AmazonJsonApi(AwsService.of("ssm"), SystemsManagerJackson, scope, credentialsProvider, rawHttp, clock, payloadMode, AwsService.of("AmazonSSM"))

    override fun put(request: PutParameter.Request): Result<PutParameter.Response, RemoteFailure> = api("PutParameter", request)

    override fun get(request: GetParameter.Request): Result<GetParameter.Response, RemoteFailure> = api("GetParameter", request)

    override fun delete(request: DeleteParameter.Request): Result<Unit, RemoteFailure> = api("DeleteParameter", request)
}
