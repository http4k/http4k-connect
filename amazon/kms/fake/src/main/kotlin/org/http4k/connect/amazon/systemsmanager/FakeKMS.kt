package org.http4k.connect.amazon.systemsmanager

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.kms.CreateKey
import org.http4k.connect.amazon.kms.DescribeKey
import org.http4k.connect.amazon.kms.Http
import org.http4k.connect.amazon.kms.KMS
import org.http4k.connect.amazon.kms.KMSJackson.auto
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.CustomerMasterKeySpec
import org.http4k.connect.amazon.model.KeyMetadata
import org.http4k.connect.amazon.model.KeyUsage
import org.http4k.connect.amazon.model.KmsKeyId
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Body
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import org.http4k.routing.bind
import org.http4k.routing.header
import org.http4k.routing.routes
import java.time.Clock
import java.util.UUID

data class StoredCMK(val keyId: KmsKeyId, val arn: ARN, val keyUsage: KeyUsage?, val customerMasterKeySpec: CustomerMasterKeySpec?)

class FakeKMS(
    private val keys: Storage<StoredCMK> = Storage.InMemory(),
    private val clock: Clock = Clock.systemDefaultZone()
) : ChaosFake() {

    override val app = routes(
        "/" bind POST to routes(
            createKey(),
            describeKey()
        )
    )

    private fun createKey() = header("X-Amz-Target", "TrentService.CreateKey") bind {
        val req = Body.auto<CreateKey.Request>().toLens()(it)
        val keyId = KmsKeyId.of(UUID.randomUUID().toString())
        val storedCMK = StoredCMK(keyId, toArn(keyId), req.KeyUsage, req.CustomerMasterKeySpec)

        keys[storedCMK.arn.value] = storedCMK

        Response(OK)
            .with(Body.auto<CreateKey.Response>().toLens()
                of CreateKey.Response(KeyMetadata(storedCMK.keyId, storedCMK.arn, AwsAccount.of("0"), req.KeyUsage)))
    }

    private fun describeKey() = header("X-Amz-Target", "TrentService.DescribeKey") bind {
        val req = Body.auto<DescribeKey.Request>().toLens()(it)

        keys[toArn(req.KeyId).value]?.let {
            Response(OK)
                .with(Body.auto<DescribeKey.Response>().toLens()
                    of DescribeKey.Response(KeyMetadata(req.KeyId, it.arn, AwsAccount.of("0"), it.keyUsage)))
        } ?: Response(BAD_REQUEST)
    }

    private fun toArn(keyId: KmsKeyId) = when {
        keyId.value.startsWith("arn") -> ARN.of(keyId.value)
        else -> ARN.of(Region.of("ldn-north-1"), AwsService.of("kms"), "key", keyId.value, AwsAccount.of("0"))
    }


    /**
     * Convenience function to get SystemsManager client
     */
    fun client() = KMS.Http(
        AwsCredentialScope("*", "kms"),
        { AwsCredentials("accessKey", "secret") }, this, clock)
}

fun main() {
    FakeKMS().start()
}
