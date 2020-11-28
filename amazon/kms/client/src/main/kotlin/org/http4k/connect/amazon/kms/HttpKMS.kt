package org.http4k.connect.amazon.kms

import dev.forkhandles.result4k.Result
import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.RemoteFailure
import org.http4k.connect.amazon.AmazonJsonApi
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.required
import org.http4k.core.HttpHandler
import org.http4k.filter.Payload
import java.time.Clock

fun KMS.Companion.Http(scope: AwsCredentialScope,
                       credentialsProvider: () -> AwsCredentials,
                       rawHttp: HttpHandler = JavaHttpClient(),
                       clock: Clock = Clock.systemDefaultZone(),
                       payloadMode: Payload.Mode = Payload.Mode.Signed) = object : KMS {
    private val api = AmazonJsonApi(AwsService.of("kms"), KMSJackson, scope, credentialsProvider, rawHttp, clock, payloadMode)

    override fun createKey(request: CreateKey.Request): Result<CreateKey.Response, RemoteFailure> =
        api.required("CreateKey", request)

    override fun describeKey(request: DescribeKey.Request): Result<DescribeKey.Response, RemoteFailure> =
        api.required("DescribeKey", request)

    override fun decrypt(request: Decrypt.Request): Result<Decrypt.Response, RemoteFailure> = api.required("Decrypt", request)

    override fun encrypt(request: Encrypt.Request): Result<Encrypt.Response, RemoteFailure> = api.required("Encrypt", request)

    override fun getPublicKey(request: GetPublicKey.Request): Result<GetPublicKey.Response, RemoteFailure> =
        api.required("GetPublicKey", request)

    override fun scheduleKeyDelete(request: ScheduleKeyDeletion.Request): Result<ScheduleKeyDeletion.Response, RemoteFailure> =
        api.required("ScheduleKeyDelete", request)

    override fun sign(request: Sign.Request): Result<Sign.Response, RemoteFailure> = api.required("", request)

    override fun verify(request: Verify.Request): Result<Verify.Response, RemoteFailure> = api.required("", request)
}
