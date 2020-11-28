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
import org.http4k.connect.amazon.kms.KMSJackson
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
import org.http4k.core.Method.POST
import org.http4k.core.Response
import org.http4k.core.Status.Companion.BAD_REQUEST
import org.http4k.core.Status.Companion.OK
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

    private fun createKey() = route<CreateKey, CreateKey.Request> {
        val keyId = KmsKeyId.of(UUID.randomUUID().toString())
        val storedCMK = StoredCMK(keyId, toArn(keyId), it.KeyUsage ?: ENCRYPT_DECRYPT, it.CustomerMasterKeySpec
            ?: CustomerMasterKeySpec.SYMMETRIC_DEFAULT)

        keys[storedCMK.arn.value] = storedCMK

        CreateKey.Response(KeyMetadata(storedCMK.keyId, storedCMK.arn, AwsAccount.of("0"), it.KeyUsage))
    }

    private fun describeKey() = route<DescribeKey, DescribeKey.Request> { req ->
        keys[toArn(req.KeyId).value]?.let {
            DescribeKey.Response(KeyMetadata(it.keyId, it.arn, AwsAccount.of("0"), it.keyUsage))
        }
    }

    private fun decrypt() = route<Decrypt, Decrypt.Request> { req ->
        keys[toArn(req.KeyId).value]?.let {
            val plainText = Base64Blob.encoded(req.CiphertextBlob.decoded().reversed())
            Decrypt.Response(KmsKeyId.of(it.arn), plainText, req.EncryptionAlgorithm ?: SYMMETRIC_DEFAULT)
        }
    }

    private fun encrypt() = route<Encrypt, Encrypt.Request> { req ->
        keys[toArn(req.KeyId).value]?.let {
            Encrypt.Response(KmsKeyId.of(it.arn), Base64Blob.encoded(req.Plaintext.decoded().reversed()), req.EncryptionAlgorithm
                ?: SYMMETRIC_DEFAULT)
        }
    }

    private fun getPublicKey() = route<GetPublicKey, GetPublicKey.Request> {
        keys[toArn(it.KeyId).value]?.let {
            GetPublicKey.Response(KmsKeyId.of(it.arn), it.customerMasterKeySpec, emptyList(), it.keyUsage, publicKey, emptyList())
        }
    }

    private fun scheduleKeyDeletion() = route<ScheduleKeyDeletion, ScheduleKeyDeletion.Request> { req ->
        keys[toArn(req.KeyId).value]?.let {
            keys[toArn(req.KeyId).value] = it.copy(deletion = Timestamp.of(MAX_VALUE))
            ScheduleKeyDeletion.Response(KmsKeyId.of(it.arn), Timestamp.of(MAX_VALUE))
        }
    }

    private fun sign() = route<Sign, Sign.Request> { req ->
        keys[toArn(req.KeyId).value]?.let {
            Sign.Response(KmsKeyId.of(it.arn),
                Base64Blob.encoded(req.SigningAlgorithm.name + req.Message.decoded()), req.SigningAlgorithm)
        }
    }

    private fun verify() = route<Verify, Verify.Request> { req ->
        keys[toArn(req.KeyId).value]?.let {
            when {
                req.Signature.decoded().startsWith(req.SigningAlgorithm.name) ->
                    Verify.Response(KmsKeyId.of(it.arn), true, req.SigningAlgorithm)
                else -> null
            }
        }
    }

    private fun toArn(keyId: KmsKeyId) = when {
        keyId.value.startsWith("arn") -> ARN.of(keyId.value)
        else -> ARN.of(Region.of("ldn-north-1"), AwsService.of("kms"), "key", keyId.value, AwsAccount.of("0"))
    }

    private inline fun <reified Wrapper, reified Req : Any> route(crossinline fn: (Req) -> Any?) =
        header("X-Amz-Target", "TrentService.${Wrapper::class.simpleName}") bind {
            fn(KMSJackson.asA(it.bodyString(), Req::class))
                ?.let { Response(OK).body(KMSJackson.asFormatString(it)) } ?: Response(BAD_REQUEST)
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
