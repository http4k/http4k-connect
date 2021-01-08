package org.http4k.connect.amazon.kms

import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.AmazonJsonFake
import org.http4k.connect.amazon.kms.action.CreateKey
import org.http4k.connect.amazon.kms.action.Decrypt
import org.http4k.connect.amazon.kms.action.Decrypted
import org.http4k.connect.amazon.kms.action.DescribeKey
import org.http4k.connect.amazon.kms.action.Encrypt
import org.http4k.connect.amazon.kms.action.Encrypted
import org.http4k.connect.amazon.kms.action.GetPublicKey
import org.http4k.connect.amazon.kms.action.KeyCreated
import org.http4k.connect.amazon.kms.action.KeyDeletionSchedule
import org.http4k.connect.amazon.kms.action.KeyDescription
import org.http4k.connect.amazon.kms.action.PublicKey
import org.http4k.connect.amazon.kms.action.ScheduleKeyDeletion
import org.http4k.connect.amazon.kms.action.Sign
import org.http4k.connect.amazon.kms.action.Signed
import org.http4k.connect.amazon.kms.action.Verify
import org.http4k.connect.amazon.kms.action.VerifyResult
import org.http4k.connect.amazon.model.ARN
import org.http4k.connect.amazon.model.AwsAccount
import org.http4k.connect.amazon.model.AwsService
import org.http4k.connect.amazon.model.Base64Blob
import org.http4k.connect.amazon.model.CustomerMasterKeySpec
import org.http4k.connect.amazon.model.EncryptionAlgorithm.SYMMETRIC_DEFAULT
import org.http4k.connect.amazon.model.KMSKeyId
import org.http4k.connect.amazon.model.KeyMetadata
import org.http4k.connect.amazon.model.KeyUsage
import org.http4k.connect.amazon.model.KeyUsage.ENCRYPT_DECRYPT
import org.http4k.connect.amazon.model.Region
import org.http4k.connect.amazon.model.Timestamp
import org.http4k.connect.storage.InMemory
import org.http4k.connect.storage.Storage
import org.http4k.core.Method.POST
import org.http4k.routing.bind
import org.http4k.routing.routes
import java.time.Clock
import java.util.UUID
import kotlin.Long.Companion.MAX_VALUE

data class StoredCMK(
    val keyId: KMSKeyId,
    val arn: ARN,
    val keyUsage: KeyUsage,
    val customerMasterKeySpec: CustomerMasterKeySpec,
    val deletion: Timestamp? = null
)

class FakeKMS(
    private val keys: Storage<StoredCMK> = Storage.InMemory(),
    private val clock: Clock = Clock.systemDefaultZone(),
) : ChaosFake() {

    private val api = AmazonJsonFake(KMSMoshi, AwsService.of("TrentService"))

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

    private fun createKey() = api.route<CreateKey> {
        val keyId = KMSKeyId.of(UUID.randomUUID().toString())
        val storedCMK = StoredCMK(
            keyId, keyId.toArn(), it.KeyUsage ?: ENCRYPT_DECRYPT, it.CustomerMasterKeySpec
                ?: CustomerMasterKeySpec.SYMMETRIC_DEFAULT
        )

        keys[storedCMK.arn.value] = storedCMK

        KeyCreated(KeyMetadata(storedCMK.keyId, storedCMK.arn, AwsAccount.of("0"), it.KeyUsage))
    }

    private fun describeKey() = api.route<DescribeKey> { req ->
        keys[req.KeyId.toArn().value]?.let {
            KeyDescription(KeyMetadata(it.keyId, it.arn, AwsAccount.of("0"), it.keyUsage))
        }
    }

    private fun decrypt() = api.route<Decrypt> { req ->
        keys[req.KeyId.toArn().value]?.let {
            val plainText = Base64Blob.encoded(req.CiphertextBlob.decoded().reversed())
            Decrypted(KMSKeyId.of(it.arn), plainText, req.EncryptionAlgorithm ?: SYMMETRIC_DEFAULT)
        }
    }

    private fun encrypt() = api.route<Encrypt> { req ->
        keys[req.KeyId.toArn().value]?.let {
            Encrypted(
                KMSKeyId.of(it.arn), Base64Blob.encoded(req.Plaintext.decoded().reversed()), req.EncryptionAlgorithm
                    ?: SYMMETRIC_DEFAULT
            )
        }
    }

    private fun getPublicKey() = api.route<GetPublicKey> {
        keys[it.KeyId.toArn().value]?.let {
            PublicKey(KMSKeyId.of(it.arn), it.customerMasterKeySpec, emptyList(), it.keyUsage, publicKey, emptyList())
        }
    }

    private fun scheduleKeyDeletion() = api.route<ScheduleKeyDeletion> { req ->
        keys[req.KeyId.toArn().value]?.let {
            keys[req.KeyId.toArn().value] = it.copy(deletion = Timestamp.of(MAX_VALUE))
            KeyDeletionSchedule(KMSKeyId.of(it.arn), Timestamp.of(MAX_VALUE))
        }
    }

    private fun sign() = api.route<Sign> { req ->
        keys[req.KeyId.toArn().value]?.let {
            Signed(
                KMSKeyId.of(it.arn),
                Base64Blob.encoded(
                    req.SigningAlgorithm.name
                        + req.Message.decoded().take(50)
                ), req.SigningAlgorithm
            )
        }
    }

    private fun verify() = api.route<Verify> { req ->
        keys[req.KeyId.toArn().value]?.let {
            when {
                req.Signature.decoded().startsWith(req.SigningAlgorithm.name) ->
                    VerifyResult(KMSKeyId.of(it.arn), true, req.SigningAlgorithm)
                else -> null
            }
        }
    }

    private fun KMSKeyId.toArn() = when {
        value.startsWith("arn") -> ARN.of(value)
        else -> ARN.of(AwsService.of("kms"), Region.of("ldn-north-1"), AwsAccount.of("0"), "key", this)
    }

    /**
     * Convenience function to get a KMS client
     */
    fun client() = KMS.Http(
        Region.of("ldn-north-1"),
        { AwsCredentials("accessKey", "secret") }, this, clock
    )
}

fun main() {
    FakeKMS().start()
}
