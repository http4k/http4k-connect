package org.http4k.connect.amazon.kms

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.client.JavaHttpClient
import org.http4k.connect.amazon.AmazonJsonApi
import org.http4k.connect.amazon.model.AwsService
import org.http4k.core.HttpHandler
import org.http4k.filter.Payload
import java.time.Clock

fun KMS.Companion.Http(scope: AwsCredentialScope,
                       credentialsProvider: () -> AwsCredentials,
                       rawHttp: HttpHandler = JavaHttpClient(),
                       clock: Clock = Clock.systemDefaultZone(),
                       payloadMode: Payload.Mode = Payload.Mode.Signed) = object : KMS {
    private val awsService = AwsService.of("kms")

    private val api = AmazonJsonApi(awsService, scope, credentialsProvider, KMSJackson, rawHttp, clock, payloadMode)

    override fun createKey(request: CreateKey.Request): CreateKey.Response {
        TODO("Not yet implemented")
    }

    override fun describe(request: DescribeKey.Request): DescribeKey.Response {
        TODO("Not yet implemented")
    }

    override fun decrypt(request: Decrypt.Request): Decrypt.Response {
        TODO("Not yet implemented")
    }

    override fun encrypt(request: Encrypt.Request): Encrypt.Response {
        TODO("Not yet implemented")
    }

    override fun getPublicKey(request: GetPublicKey.Request): GetPublicKey.Response {
        TODO("Not yet implemented")
    }

    override fun scheduleKeyDelete(request: ScheduleKeyDeletion.Request): ScheduleKeyDeletion.Response {
        TODO("Not yet implemented")
    }

    override fun sign(request: Sign.Request): Sign.Response {
        TODO("Not yet implemented")
    }

    override fun verify(request: Verify.Request): Verify.Response {
        TODO("Not yet implemented")
    }
}
