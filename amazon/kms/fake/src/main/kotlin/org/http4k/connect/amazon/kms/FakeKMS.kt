package org.http4k.connect.amazon.kms

import org.http4k.aws.AwsCredentialScope
import org.http4k.aws.AwsCredentials
import org.http4k.connect.ChaosFake
import org.http4k.connect.amazon.AmazonJsonFake
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
import org.http4k.routing.bind
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

    private val api = AmazonJsonFake(KMSJackson, AwsService.of("TrentService"))

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

    private fun createKey() = api.route<CreateKey, CreateKey.Request> {
        val keyId = KmsKeyId.of(UUID.randomUUID().toString())
        val storedCMK = StoredCMK(keyId, toArn(keyId), it.KeyUsage ?: ENCRYPT_DECRYPT, it.CustomerMasterKeySpec
            ?: CustomerMasterKeySpec.SYMMETRIC_DEFAULT)

        keys[storedCMK.arn.value] = storedCMK

        CreateKey.Response(KeyMetadata(storedCMK.keyId, storedCMK.arn, AwsAccount.of("0"), it.KeyUsage))
    }

    private fun describeKey() = api.route<DescribeKey, DescribeKey.Request> { req ->
        keys[toArn(req.KeyId).value]?.let {
            DescribeKey.Response(KeyMetadata(it.keyId, it.arn, AwsAccount.of("0"), it.keyUsage))
        }
    }

    private fun decrypt() = api.route<Decrypt, Decrypt.Request> { req ->
        keys[toArn(req.KeyId).value]?.let {
            val plainText = Base64Blob.encoded(req.CiphertextBlob.decoded().reversed())
            Decrypt.Response(KmsKeyId.of(it.arn), plainText, req.EncryptionAlgorithm ?: SYMMETRIC_DEFAULT)
        }
    }

    private fun encrypt() = api.route<Encrypt, Encrypt.Request> { req ->
        keys[toArn(req.KeyId).value]?.let {
            Encrypt.Response(KmsKeyId.of(it.arn), Base64Blob.encoded(req.Plaintext.decoded().reversed()), req.EncryptionAlgorithm
                ?: SYMMETRIC_DEFAULT)
        }
    }

    private fun getPublicKey() = api.route<GetPublicKey, GetPublicKey.Request> {
        keys[toArn(it.KeyId).value]?.let {
            GetPublicKey.Response(KmsKeyId.of(it.arn), it.customerMasterKeySpec, emptyList(), it.keyUsage, publicKey, emptyList())
        }
    }

    private fun scheduleKeyDeletion() = api.route<ScheduleKeyDeletion, ScheduleKeyDeletion.Request> { req ->
        keys[toArn(req.KeyId).value]?.let {
            keys[toArn(req.KeyId).value] = it.copy(deletion = Timestamp.of(MAX_VALUE))
            ScheduleKeyDeletion.Response(KmsKeyId.of(it.arn), Timestamp.of(MAX_VALUE))
        }
    }

    private fun sign() = api.route<Sign, Sign.Request> { req ->
        keys[toArn(req.KeyId).value]?.let {
            Sign.Response(KmsKeyId.of(it.arn),
                Base64Blob.encoded(req.SigningAlgorithm.name
                    + req.Message.decoded().take(50)), req.SigningAlgorithm)
        }
    }

    private fun verify() = api.route<Verify, Verify.Request> { req ->
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
