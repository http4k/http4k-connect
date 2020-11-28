package org.http4k.connect.amazon.systemsmanager

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.kms.CreateKey
import org.http4k.connect.amazon.kms.Decrypt
import org.http4k.connect.amazon.kms.DescribeKey
import org.http4k.connect.amazon.kms.Encrypt
import org.http4k.connect.amazon.kms.GetPublicKey
import org.http4k.connect.amazon.kms.Http
import org.http4k.connect.amazon.kms.KMS
import org.http4k.connect.amazon.kms.KMSJackson.auto
import org.http4k.connect.amazon.kms.ScheduleKeyDeletion
import org.http4k.connect.amazon.kms.Sign
import org.http4k.connect.amazon.kms.Verify
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.CustomerMasterKeySpec
import org.http4k.connect.amazon.model.EncryptionAlgorithm.SYMMETRIC_DEFAULT
import org.http4k.connect.amazon.model.KeyMetadata
import org.http4k.connect.amazon.model.KeyUsage
import org.http4k.connect.amazon.model.KeyUsage.ENCRYPT_DECRYPT
import org.http4k.connect.amazon.model.KmsKeyId
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.Timestamp
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
import kotlin.Long.Companion.MAX_VALUE

data class StoredCMK(
    val keyId: KmsKeyId,
    val arn: ARN,
    val keyUsage: KeyUsage,
    val customerMasterKeySpec: CustomerMasterKeySpec,
    val deletion: Timestamp? = null
)

class FakeKMS(
    private val keys: Storage<StoredCMK> = Storage.InMemory(),
    private val clock: Clock = Clock.systemDefaultZone(),
) : ChaosFake() {

    private val publicKey by lazy {
        Base64Blob.encoded(this::class.java.classLoader.getResource("id_example.pub")!!.readText())
    }

    private val privateKey by lazy {
        Base64Blob.encoded(this::class.java.classLoader.getResource("id_example")!!.readText())
    }

    override val app = routes(
        "/" bind POST to routes(
            createKey(),
            describeKey(),
            decrypt(),
            encrypt(),
            getPublicKey(),
            scheduleKeyDeletion(),
            sign(),
            verify()
        )
    )

    private fun createKey() = header("X-Amz-Target", "TrentService.CreateKey") bind {
        val req = Body.auto<CreateKey.Request>().toLens()(it)
        val keyId = KmsKeyId.of(UUID.randomUUID().toString())
        val storedCMK = StoredCMK(keyId, toArn(keyId), req.KeyUsage ?: ENCRYPT_DECRYPT, req.CustomerMasterKeySpec
            ?: CustomerMasterKeySpec.SYMMETRIC_DEFAULT)

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

    private fun decrypt() = header("X-Amz-Target", "TrentService.Decrypt") bind {
        val req = Body.auto<Decrypt.Request>().toLens()(it)
        val plainText = Base64Blob.encoded(req.CiphertextBlob.decoded().reversed())

        keys[toArn(req.KeyId).value]?.let {
            Response(OK)
                .with(Body.auto<Decrypt.Response>().toLens()
                    of Decrypt.Response(KmsKeyId.of(it.arn), plainText, req.EncryptionAlgorithm ?: SYMMETRIC_DEFAULT))
        } ?: Response(BAD_REQUEST)
    }

    private fun encrypt() = header("X-Amz-Target", "TrentService.Encrypt") bind {
        val req = Body.auto<Encrypt.Request>().toLens()(it)
        val encrypted = Base64Blob.encoded(req.Plaintext.decoded().reversed())

        keys[toArn(req.KeyId).value]?.let {
            Response(OK)
                .with(Body.auto<Encrypt.Response>().toLens()
                    of Encrypt.Response(KmsKeyId.of(it.arn), encrypted, req.EncryptionAlgorithm ?: SYMMETRIC_DEFAULT))
        } ?: Response(BAD_REQUEST)
    }

    private fun getPublicKey() = header("X-Amz-Target", "TrentService.GetPublicKey") bind {
        val req = Body.auto<GetPublicKey.Request>().toLens()(it)

        keys[toArn(req.KeyId).value]?.let {
            Response(OK)
                .with(Body.auto<GetPublicKey.Response>().toLens()
                    of GetPublicKey.Response(KmsKeyId.of(it.arn), it.customerMasterKeySpec, emptyList(), it.keyUsage, publicKey, emptyList()))
        } ?: Response(BAD_REQUEST)
    }

    private fun scheduleKeyDeletion() = header("X-Amz-Target", "TrentService.ScheduleKeyDeletion") bind {
        val req = Body.auto<ScheduleKeyDeletion.Request>().toLens()(it)

        keys[toArn(req.KeyId).value]?.let {
            keys[toArn(req.KeyId).value] = it.copy(deletion = Timestamp.of(MAX_VALUE))
            Response(OK)
                .with(Body.auto<ScheduleKeyDeletion.Response>().toLens()
                    of ScheduleKeyDeletion.Response(KmsKeyId.of(it.arn), Timestamp.of(MAX_VALUE)))
        } ?: Response(BAD_REQUEST)
    }

    private fun sign() = header("X-Amz-Target", "TrentService.Sign") bind {
        val req = Body.auto<Sign.Request>().toLens()(it)

        keys[toArn(req.KeyId).value]?.let {
            Response(OK)
                .with(Body.auto<Sign.Response>().toLens()
                    of Sign.Response(KmsKeyId.of(it.arn),
                    Base64Blob.encoded(req.SigningAlgorithm.name + req.Message.decoded()), req.SigningAlgorithm))
        } ?: Response(BAD_REQUEST)
    }

    private fun verify() = header("X-Amz-Target", "TrentService.Verify") bind {
        val req = Body.auto<Verify.Request>().toLens()(it)

        keys[toArn(req.KeyId).value]?.let {
            if(req.Signature.decoded().startsWith(req.SigningAlgorithm.name)) {
                Response(OK)
                    .with(Body.auto<Verify.Response>().toLens()
                        of Verify.Response(KmsKeyId.of(it.arn), true, req.SigningAlgorithm))
            } else {
                Response(BAD_REQUEST)
            }
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
