package org.http4k.connect.amazon.systemsmanager

import dev.forkhandles.result4k.Result
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AmazonJsonApi
import org.http4k.connect.amazon.model.AwsService
import org.http4k.core.HttpHandler
import org.http4k.filter.Payload
import java.time.Clock

fun SystemsManager.Companion.Http(scope: AwsCredentialScope,
                                  credentialsProvider: () -> AwsCredentials,
                                  rawHttp: HttpHandler = JavaHttpClient(),
                                  clock: Clock = Clock.systemDefaultZone(),
                                  payloadMode: Payload.Mode = Payload.Mode.Signed) = object : SystemsManager {
    private val api = AmazonJsonApi(AwsService.of("ssm"), SystemsManagerJackson, scope, credentialsProvider, rawHttp, clock, payloadMode)

    /**
     * POST / HTTP/1.1
    Host: ssm.us-east-2.amazonaws.com
    Accept-Encoding: identity
    Content-Length: 29
    X-Amz-Target: AmazonSSM.GetParameter
    X-Amz-Date: 20180316T005724Z
    User-Agent: aws-cli/1.11.180 Python/2.7.9 Windows/8 botocore/1.7.38
    Content-Type: application/x-amz-json-1.1
    Authorization: AWS4-HMAC-SHA256 Credential=AKIAIOSFODNN7EXAMPLE/20180316/us-east-2/ssm/aws4_request,
    SignedHeaders=content-type;host;x-amz-date;x-amz-target, Signature=39c3b3042cd2aEXAMPLE

     */
    override fun put(request: PutParameter.Request): Result<PutParameter.Response, RemoteFailure> {
        TODO("Not yet implemented")
    }

    override fun get(request: GetParameter.Request): Result<GetParameter.Response?, RemoteFailure> {
        TODO("Not yet implemented")
    }

    override fun delete(request: DeleteParameter.Request): Result<DeleteParameter.Response?, RemoteFailure> {
        TODO("Not yet implemented")
    }

}
