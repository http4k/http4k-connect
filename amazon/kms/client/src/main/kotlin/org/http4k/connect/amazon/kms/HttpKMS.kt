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
    private val api = AmazonJsonApi(AwsService.of("kms"), KMSMoshi, scope, credentialsProvider, rawHttp, clock, payloadMode, AwsService.of("TrentService"))

    override operator fun invoke(request: CreateKeyRequest): Result<CreateKeyResponse, RemoteFailure> =
        api("CreateKey", request)

    override operator fun invoke(request: DescribeKeyRequest): Result<DescribeKeyResponse, RemoteFailure> =
        api("DescribeKey", request)

    override operator fun invoke(request: DecryptRequest): Result<DecryptResponse, RemoteFailure> = api("Decrypt", request)

    override operator fun invoke(request: EncryptRequest): Result<EncryptResponse, RemoteFailure> = api("Encrypt", request)

    override operator fun invoke(request: GetPublicKeyRequest): Result<GetPublicKeyResponse, RemoteFailure> =
        api("GetPublicKey", request)

    override operator fun invoke(request: ScheduleKeyDeletionRequest): Result<ScheduleKeyDeletionResponse, RemoteFailure> =
        api("ScheduleKeyDeletion", request)

    override operator fun invoke(request: SignRequest): Result<SignResponse, RemoteFailure> = api("Sign", request)

    override operator fun invoke(request: VerifyRequest): Result<VerifyResponse, RemoteFailure> = api("Verify", request)
}
