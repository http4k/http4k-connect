package org.http4k.connect.amazon.kms

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

fun KMS.Companion.Http(scope: AwsCredentialScope,
                       credentialsProvider: () -> AwsCredentials,
                       rawHttp: HttpHandler = JavaHttpClient(),
                       clock: Clock = Clock.systemDefaultZone(),
                       payloadMode: Payload.Mode = Payload.Mode.Signed) = object : KMS {
    private val api = AmazonJsonApi(AwsService.of("kms"), KMSJackson, scope, credentialsProvider, rawHttp, clock, payloadMode, AwsService.of("TrentService"))

    override fun create(request: CreateKey.Request): Result<CreateKey.Response, RemoteFailure> =
        api("CreateKey", request)

    override fun describe(request: DescribeKey.Request): Result<DescribeKey.Response, RemoteFailure> =
        api("DescribeKey", request)

    override fun decrypt(request: Decrypt.Request): Result<Decrypt.Response, RemoteFailure> = api("Decrypt", request)

    override fun encrypt(request: Encrypt.Request): Result<Encrypt.Response, RemoteFailure> = api("Encrypt", request)

    override fun getPublicKey(request: GetPublicKey.Request): Result<GetPublicKey.Response, RemoteFailure> =
        api("GetPublicKey", request)

    override fun scheduleDeletion(request: ScheduleKeyDeletion.Request): Result<ScheduleKeyDeletion.Response, RemoteFailure> =
        api("ScheduleKeyDeletion", request)

    override fun sign(request: Sign.Request): Result<Sign.Response, RemoteFailure> = api("Sign", request)

    override fun verify(request: Verify.Request): Result<Verify.Response, RemoteFailure> = api("Verify", request)
}
